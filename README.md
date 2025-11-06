# GRISON

![grison image](./grison.jpg)

> A grison (/ˈɡrɪzən/, /ˈɡraɪsən/) is any mustelid in the genus Galictis.
> Native to Central and South America, the genus contains two extant species:
> the greater grison (Galictis vittata), which is found widely in South America,
> through Central America to southern Mexico; and the lesser grison (Galictis cuja),
> which is restricted to the southern half of South America.

[source](https://en.wikipedia.org/wiki/Galictis)

## Assumptions

Based on the assignment, there are a few unclear points.
Let's describe a few assumptions, which can be specified by a documentation.
I will name them as if they were JIRA tickets under `Assumptions` project (abbr. ASS).
Some parts of the code refer to these in kdoc comments.

### ASS-1
There can only be max one card at any given time. The underlying implementation (i.e. storage)
does support multi-card approach, but this is considered out of scope for this project. However
some parts of the codebase include commented-out code which refers to changes needed to better 
support multi-card feature.

### ASS-2
Each card has it's own unique identifier (UUID) which is different to the activation code.
This is based on assumption, that in real life, list of available cards could be delivered to the
app by an API server.

### ASS-3
Generating an activation card (scratching) is considered a remote operation involving communication
with an API server. There is an artificial delay implemented to simulate this.
The fact that the activation code is effectively a mutable property assigned to the card during
scratching process is based on this assumption.

### ASS-4
There is no time limit for a scratched card to be activated. This means once the card is scratched
the activation code is generated and stored in database.

### ASS-5
There is no time limit for an activated card to be used. This means once the card is activated
the card can be displayed until reset is triggered by a user. The reset action is deliberately
introduced on top of requirements, to make user flows repeatable when testing/demoing the app.

### ASS-6
There is no requirement for securing the network communication.

### ASS-7
Securing any PII rendered/printed/logged anywhere within this project is not a scope of this assignment.
This includes displaying error messages that might include information coming from an API service. 

## Design Decisions

The app is using the MVI pattern, connecting Compose Screens with ViewModel by using an unidirectional
data flow.
1. The Screen triggers events (intent)
2. ViewModel reacts and updates the State
3. The State is consumed by the Screen

The Data is stored in a local Room Database to make card operations persistent between app runs.

Error handling is simple - using just a `kotlin.Result` wrapper for operations that can fail.
A more sophisticated approach would require a set of custom return types, i.e. a sealed interface
with success type and multiple failure subtypes based on the failure cause.

State management in Compose screens follows the data class pattern. There are other patterns
which could one consider better or worse for specific projects. An example would be a sealed
interface approach, where state changes follow strict type-safe transitions. For a project of this
scale I chose to use a data class. Nothing more, nothing less behind this decision.

Note: Some composables are reused from my previous projects

## Architecture

This project is built by applying clean architecture concepts, separating the responsibilities into different layers (or modules).
If you’d like to know more about the core concept and responsibilities of layers at a high level,
you should refer to the famous Uncle bob article on the topic https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html


## TODOs

Error Handling Improvements - We currently rely on DB operations to succeed (i.e. creating a single card initially).
If this fails for some reason (i.e. corrupted DB), we need to inform user instead of waiting for
`CardsRepository.streamCard` to return a non-empty instance. This could be done by either implementing
an error state in the repository, or using a timeout in each relevant place. However in real life
this is probably coming from an API server, so let's keep this open.

Localization - There is no localization. All string resources are currently hardcoded.
It's not a scope of this project to show how localization is usually done. If you need to ask
an engineer how to do this, he/she is probably not a good fit for this role.

Notifications / Info Messages - for background operations where we fire-and-forget (Activation)
a global snackbar or any sort of popup message would be helpful to inform user about operation
progress (i.e. when activation screen is closed before completing the operation). For the scope
of this project, this is considered nice-to-have, because essentially the underlying data object
will update the state of an active screen if we exit the Activation Screen before completion.

Activation Error Handling - if there is an issue during activation, currently the project only
handles on-screen errors. However if we leave the screen and there is an error, we don't
explicitly show the error modal globally. This can be approached by number of ways - i.e. a global
ModalRepository which would accept any type of Modal and the compose navigation code would take
care of displaying the Modal.
Due to timeboxing this assignment, this is out of scope of this project.

Accessibility - Out of scope due to limited timebox of this assignment.

Proguard / Obfuscation - Out of scope due to limited timebox of this assignment.

Unit Tests - MainDispatcherRule to avoid duplicated `Dispatchers.setMain`/`Dispatchers.resetMain` calls

UI - presentation of the ScratchCard composable is deliberately chosen to be descriptive and
test-focused to ease demoing / testing. In real world scenario, the UI/UX would be different and more user-focused.

UI #2 - Main Screen action visibility handling is deliberately skipped, to provide a way to
enter screens in different states, to be able to verify how the screen behaves in different ScratchCard states.

