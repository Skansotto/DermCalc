# DermCalc

Applicazione Android per il calcolo e il monitoraggio degli indici clinici usati in
dermatologia: **PASI**, **EASI**, **BMI** e **BSA**. I punteggi possono essere collegati a un
paziente e seguiti nel tempo, con storico, grafico dell'andamento e referto in PDF.

L'archivio è interamente locale: nessun dato lascia il dispositivo, non esiste un backend e
non viene effettuata alcuna chiamata di rete. Il backup è un file JSON gestito dall'utente.

---

## Indice

- [Requisiti e build](#requisiti-e-build)
- [Architettura](#architettura)
- [Struttura dei sorgenti](#struttura-dei-sorgenti)
- [Modello dati](#modello-dati)
- [Logica clinica](#logica-clinica)
- [Design system](#design-system)
- [Navigazione e flusso di avvio](#navigazione-e-flusso-di-avvio)
- [Sicurezza e privacy](#sicurezza-e-privacy)
- [Backup, esportazione e referto](#backup-esportazione-e-referto)

---

## Requisiti e build

| | |
|---|---|
| Linguaggio | Kotlin 2.2.10 |
| Build | Gradle Kotlin DSL, modulo singolo `:app` |
| AGP | 9.3.1 |
| `minSdk` | 24 |
| `compileSdk` / `targetSdk` | 37 |
| Java | 11 (source e target) |
| UI | Jetpack Compose, BOM `2026.02.01`, Material 3 |
| Persistenza | Room 2.8.4 (con KSP) |
| Navigazione | Navigation Compose 2.9.8 |
| Biometria | `androidx.biometric` 1.1.0 |

Le versioni sono centralizzate in `gradle/libs.versions.toml`. Non sono presenti layout XML:
l'intera interfaccia è realizzata in Compose.

Compilazione del pacchetto di debug:

```bash
./gradlew :app:assembleDebug
```

Installazione sul dispositivo collegato:

```bash
./gradlew installDebug
```

---

## Architettura

Il progetto adotta il pattern MVVM, con una separazione in tre livelli e una sola regola di
fondo: **lo stato e la logica risiedono nei ViewModel**, mentre i composable si limitano a
disegnare.

```
UI (Compose)
  └─ osserva StateFlow, invia eventi
ViewModel
  └─ possiede lo stato, applica le regole, chiama i repository
Repository
  └─ unico punto di accesso a Room e alle preferenze
```

Il livello `domain/` è trasversale ai tre: contiene le regole cliniche pure (bande di
severità, formule, formattazione), senza dipendenze da Android a eccezione di
`androidx.compose.ui.graphics.Color`, necessario per i colori di severità, che appartengono
alla semantica clinica e non al tema.

Le dipendenze sono costruite manualmente in `DermCalcApplication` tramite `by lazy` e passate
ai ViewModel attraverso `ViewModelProvider.Factory`: non è impiegato alcun framework di
dependency injection.

```kotlin
class DermCalcApplication : Application() {
    private val database by lazy { DermCalcDatabase.getInstance(this) }
    val pazienteRepository by lazy { PazienteRepository(database.pazienteDao()) }
    val misurazioneRepository by lazy { MisurazioneRepository(database.misurazioneDao()) }
    val backupRepository by lazy { BackupRepository(pazienteRepository, misurazioneRepository) }
    val pinManager by lazy { PinManager(this) }
    val onboardingManager by lazy { OnboardingManager(this) }
    val profiloManager by lazy { ProfiloManager(this) }
    val impostazioniManager by lazy { ImpostazioniManager(this) }
    val sessione by lazy { SessioneCorrente() }
}
```

---

## Struttura dei sorgenti

Circa 8.400 righe di Kotlin in `app/src/main/java/com/casati/dermcalc/`.

```
data/
  local/          Room (entità, DAO, database, converter) + preferenze
    Converters.kt          TypeConverter per gli enum persistiti
    DermCalcDatabase.kt    database, versione 2
    MisurazioneDao.kt      DAO misurazioni
    MisurazioneEntity.kt   entità misurazione
    PazienteDao.kt         DAO pazienti
    PazienteEntity.kt      entità paziente
    ImpostazioniManager.kt preferenza sblocco biometrico
    OnboardingManager.kt   flag guida introduttiva vista
    PinManager.kt          hash del PIN con salt
    ProfiloManager.kt      account locale del medico
    SessioneCorrente.kt    paziente collegato, in memoria
    Sesso.kt / TipoCalcolo.kt
  referto/
    GeneratoreReferto.kt   PDF via android.graphics.pdf.PdfDocument
  repository/
    PazienteRepository.kt
    MisurazioneRepository.kt
    BackupRepository.kt    esportazione, importazione, scheda singola

domain/            regole cliniche, indipendenti dalla UI
  Indice.kt        distretti, parametri clinici, formula PASI/EASI
  Severita.kt      bande di severità, colori e formattazione dei punteggi
  Formattazione.kt date, età, percentuali, nome completo

navigation/
  Screen.kt          rotte tipizzate
  DermCalcNavHost.kt  grafo, ViewModel condivisi, condivisione file

ui/
  components/      libreria del design system, riusabile
  screens/         una cartella per schermata, ognuna con il suo ViewModel
    auth/ bmi/ bsa/ calcolatore/ home/ impostazioni/ onboarding/ pazienti/ profilo/
  shared/          selettore paziente condiviso fra Home e calcolatori
  theme/           colori, tipografia, tema
```

---

## Modello dati

Due tabelle, legate da un vincolo di integrità referenziale.

```kotlin
@Entity(tableName = "pazienti")
data class PazienteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val cognome: String,
    val sesso: Sesso,          // FEMMINA | MASCHIO | ALTRO
    val dataNascita: Long      // epoch millis
)

@Entity(
    tableName = "misurazioni",
    foreignKeys = [ForeignKey(
        entity = PazienteEntity::class,
        parentColumns = ["id"], childColumns = ["pazienteId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("pazienteId")]
)
data class MisurazioneEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pazienteId: Long,
    val tipo: TipoCalcolo,     // BMI | BSA | PASI | EASI
    val risultato: Double,
    val data: Long             // epoch millis
)
```

La clausola `onDelete = CASCADE` fa sì che l'eliminazione di un paziente rimuova anche le sue
misurazioni, senza che ciò debba essere gestito nel codice applicativo.

Gli enum sono persistiti **per nome** tramite `Converters`, non per ordinale: l'aggiunta di
una voce a `TipoCalcolo` o a `Sesso` non invalida i dati già memorizzati.

Il database è alla **versione 2** e utilizza `fallbackToDestructiveMigration(true)`. Si tratta
di una scelta consapevole per la fase di sviluppo: a ogni variazione dello schema l'archivio
locale viene ricreato da zero. **Prima della distribuzione va sostituita con migrazioni
esplicite**, in caso contrario un aggiornamento comporterebbe la perdita dei dati clinici
degli utenti.

Le preferenze (profilo, PIN, onboarding, biometria) sono conservate in quattro file
`SharedPreferences` distinti, uno per ciascuna responsabilità.

---

## Logica clinica

### PASI ed EASI

I due indici condividono struttura, pesi e formula, e differiscono unicamente per i parametri
valutati. Per questo motivo sono realizzati con **un solo ViewModel e una sola schermata**
(`ui/screens/calcolatore/`), con il tipo passato come argomento di navigazione.

```
punteggio = Σ  peso(distretto) × (somma parametri) × area(distretto)
```

| Distretto | Sigla | Peso |
|---|---|---|
| Testa e collo | H | 0,1 |
| Arti superiori | U | 0,2 |
| Tronco | T | 0,3 |
| Arti inferiori | L | 0,4 |

L'area è un punteggio discreto 0–6 (0%, 1–9, 10–29, 30–49, 50–69, 70–89, 90–100).

I parametri valutati per ciascun distretto:

- **PASI** — Eritema, Indurimento, Desquamazione, ciascuno 0–4
- **EASI** — Eritema, Edema/papule, Escoriazioni, Lichenificazione, ciascuno 0–3

Il totale è arrotondato a un decimale e ricalcolato a ogni tocco: non è previsto alcun
pulsante di calcolo. Per entrambi gli indici il range è 0–72.

### Bande di severità

Sono definite in `domain/Severita.kt`. Un punteggio pari esattamente a **0** dispone di una
banda propria, "assente": lo zero non rappresenta una forma lieve bensì l'assenza di malattia,
e nella psoriasi la clearance completa corrisponde all'obiettivo di risposta PASI 100.

**PASI** — soglie adottate dal progetto:

| Punteggio | Classe |
|---|---|
| 0 | assente |
| < 5 | lieve |
| 5 – 10 | moderata |
| > 10 | severa |

**EASI** — fasce dello studio di interpretabilità di Leshem (2015), riferimento standard per
tradurre il punteggio EASI in una classe di severità:

| Punteggio | Classe |
|---|---|
| 0 | assente |
| 0,1 – 1 | quasi assente |
| 1,1 – 7 | lieve |
| 7,1 – 21 | moderata |
| 21,1 – 50 | severa |
| > 50 | molto severa |

Ogni banda definisce il proprio colore, la tinta di sfondo, il colore del segmento sulla barra
e una nota clinica, mostrata nella schermata di risultato.

### BMI

`peso(kg) / altezza(m)²`. Peso e altezza si regolano a passi (0,5 kg e 1 cm) anziché da
tastiera: in visita si parte da un valore plausibile e lo si corregge per approssimazioni
successive. Le categorie sono sottopeso (< 18,5), normopeso (18,5–24,9), sovrappeso
(25–29,9) e obesità (≥ 30).

### BSA

Regola del nove di Wallace, con ciascun distretto suddiviso nella faccia **anteriore** e in
quella **posteriore**, poiché in visita si osserva un lato alla volta:

| Distretto | Anteriore | Posteriore |
|---|---|---|
| Testa e collo | 4,5% | 4,5% |
| Tronco (torace e addome / schiena) | 18% | 18% |
| Braccio destro | 4,5% | 4,5% |
| Braccio sinistro | 4,5% | 4,5% |
| Gamba destra | 9% | 9% |
| Gamba sinistra | 9% | 9% |
| Area genitale | 1% | — |

La somma dei distretti è pari al 100%. A essi si affianca la **regola del palmo** per le
lesioni sparse: un palmo del paziente vale circa l'1%, con un contatore 0–20. Il totale è
limitato a 100 e, poiché le singole facce valgono 4,5, è rappresentato da un `Double`: il
mezzo punto viene mostrato soltanto quando è effettivamente presente (`4,5%` con una sola
faccia selezionata, `9%` con entrambe).

### Variazioni nel tempo

Ogni misurazione è confrontata con la **precedente dello stesso indice**, individuata sul
timestamp completo e non sul giorno o sul mese, dal momento che nella stessa giornata possono
essere registrate più misurazioni. Il confronto è stretto (`data < corrente`), così che due
voci con timestamp identico — situazione che si verifica reimportando lo stesso backup — non
finiscano per confrontarsi tra loro.

In dermatologia una diminuzione rappresenta un miglioramento: il verde è quindi riservato ai
delta negativi e il rosso a quelli positivi.

---

## Design system

### Colori (`ui/theme/Color.kt`)

Il fondo non è mai bianco puro (`#F7F5F1`), risultando così meno abbagliante sotto la luce di
un ambulatorio. L'indaco `#2B3A67` è **l'unico colore d'azione**, in modo che verde, ambra e
rosso restino liberi di significare soltanto severità clinica.

| Ruolo | Colore |
|---|---|
| Sfondo | `#F7F5F1` |
| Superficie | `#FFFFFF` |
| Azione / marchio | `#2B3A67` |
| Severità lieve | `#2F7A57` |
| Severità moderata | `#A8690A` |
| Severità severa | `#B1402C` |
| Severità molto severa | `#8C2D1D` |

Il tema è **fisso**: il dynamic color di sistema non viene utilizzato, perché i colori di
severità devono risultare identici su ogni dispositivo.

### Tipografia (`ui/theme/Type.kt`)

Due famiglie, incluse nel pacchetto dell'applicazione in `res/font/`:

- **Plus Jakarta Sans** (Regular → ExtraBold) per il testo
- **IBM Plex Mono** (Regular, Medium, SemiBold) per **tutti i numeri**

La scelta del monospaziato è funzionale: le cifre non si spostano mentre il punteggio cambia
in tempo reale, e l'applicazione assume l'aspetto di uno strumento di misura. Oltre alla scala
Material sono definiti `MonoGrande`, `MonoMedio`, `MonoPiccolo`, `MonoMinuto` ed
`EtichettaSezione`.

### Componenti (`ui/components/`)

| File | Contenuto |
|---|---|
| `DesignSystem.kt` | carte, pulsanti, chip, selettore segmentato, barra di severità, campi, contatori, interruttore |
| `PannelloInferiore.kt` | unico contenitore modale: informazione, elenco (con ricerca), conferma |
| `BarraNavigazione.kt` | navigazione a tre sezioni, icone disegnate con `Canvas` |
| `Intestazione.kt` | intestazione di dettaglio e piede d'azione ancorato |
| `CampoRicerca.kt` | campo di ricerca condiviso da elenco pazienti e selettore |
| `Marchio.kt` | marchio dermatoscopio, stesso segno dell'icona di sistema |
| `MascheraData.kt` | `VisualTransformation` per la data di nascita |
| `Avviso.kt` | conferma testuale temporanea in fondo allo schermo |

Il **selettore segmentato** sostituisce gli slider per i punteggi discreti: sulle scale 0–3 e
0–4 un pulsante per ciascun valore risulta più leggibile e più rapido da toccare di un
cursore.

### Marchio e icona

Il marchio raffigura un dermatoscopio, ovvero la lente con cui si osserva la lesione da
vicino, con il punto pieno al centro a rappresentare la lesione a fuoco. Il segno è definito
una sola volta in `res/drawable/ic_dermatoscopio.xml` ed è riusato sia per l'icona adattiva,
con relativo livello monocromatico, sia per il marchio all'interno dell'applicazione. Per
Android 24–25, che non supporta le icone adattive, sono forniti i bitmap in tutte e cinque le
densità.

---

## Navigazione e flusso di avvio

`MainActivity` determina il contenuto da mostrare prima del grafo di navigazione, secondo
quest'ordine:

```
profilo non registrato  → Registrazione (account locale)
PIN non configurato     → Creazione PIN
guida non completata    → Onboarding
non sbloccato           → Sblocco
altrimenti              → DermCalcNavHost
```

Il `NavHost` comprende tre rotte radice dotate di barra di navigazione (Home, Pazienti,
Impostazioni) e rotte di dettaglio che la nascondono, per lasciare spazio al contenuto e al
piede d'azione.

Alcuni ViewModel sono istanziati a livello di `NavHost` e condivisi fra più schermate:
`CalcolatoreViewModel` — poiché il calcolatore e la relativa schermata di risultato devono
leggere gli stessi valori — `SelezionePazienteViewModel` e i ViewModel di BMI e BSA. Quello
della scheda paziente è invece istanziato per singola destinazione, con
`key = "paziente-$id"`.

### Sessione paziente

`SessioneCorrente` conserva il paziente a cui vengono attribuite le misurazioni ed è
**condiviso da tutti i calcolatori**: viene scelto una volta e resta attivo finché non lo si
modifica. Vive esclusivamente in memoria, per cui a ogni avvio si riparte in modalità anonima.
È una scelta voluta: una misurazione non deve poter essere attribuita per errore al paziente
della sessione precedente.

Un calcolo anonimo non viene salvato e costituisce una modalità d'uso legittima, non un
ripiego.

---

## Sicurezza e privacy

- **PIN a 6 cifre**, richiesto a ogni avvio. Non viene mai salvato in chiaro: è conservato
  soltanto un hash SHA-256 con salt casuale a 16 byte (`PinManager`), e la verifica ricalcola
  l'hash.
- **Sblocco biometrico** opzionale tramite `androidx.biometric`, con `BIOMETRIC_WEAK`. Il
  pulsante compare solo se il dispositivo dispone di una biometria utilizzabile e l'opzione è
  attiva.
- **Nessuna rete.** L'applicazione non dichiara il permesso `INTERNET` e non effettua
  richieste.
- **Nessuna analitica**, nessun identificatore, nessuna telemetria.
- I file generati (referto ed esportazioni) risiedono nella cache e sono condivisi con altre
  applicazioni tramite `FileProvider`, con permesso di lettura concesso per singola richiesta.

> **Attenzione — la cifratura a riposo non è implementata.** Il database Room e le
> `SharedPreferences` sono in chiaro all'interno della sandbox dell'applicazione. In futuro
> potranno essere introdotti SQLCipher per Room e Jetpack Security per le preferenze.

---

## Backup, esportazione e referto

### Formato del backup

JSON, versionato, prodotto e letto da `BackupRepository`:

```json
{
  "versione": 1,
  "esportatoIl": 1788209182067,
  "pazienti": [
    { "id": 1, "nome": "Elena", "cognome": "Bianchi",
      "sesso": "FEMMINA", "dataNascita": 698022000000 }
  ],
  "misurazioni": [
    { "pazienteId": 1, "tipo": "PASI", "risultato": 1.4, "data": 1788208957558 }
  ]
}
```

L'importazione **aggiunge** i dati senza cancellare l'archivio esistente. Gli id presenti nel
file non vengono riutilizzati: viene costruita una mappa dal vecchio id a quello assegnato dal
database locale, e i riferimenti delle misurazioni sono rinumerati di conseguenza. Le voci
malformate vengono ignorate e non causano il fallimento dell'intera importazione.

Il salvataggio e l'apertura passano dal selettore di sistema (`CreateDocument` /
`OpenDocument`): il file resta all'esterno dell'applicazione e sotto il controllo dell'utente.

### Referto PDF

`GeneratoreReferto` costruisce il documento con `android.graphics.pdf.PdfDocument`, senza
librerie esterne, su una pagina A4 (595 × 842 pt). Il referto riporta l'intestazione, i dati
del medico che firma, l'anagrafica del paziente e l'elenco delle misurazioni in ordine
cronologico inverso. Il file viene scritto nella cache e trasmesso ad altre applicazioni
tramite un intent di condivisione.

Dal relativo menu è inoltre possibile esportare in JSON la **scheda del singolo paziente**.
