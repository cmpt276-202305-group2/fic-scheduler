# Software Architecture

From a logical perspective, the software is structured as a stack. We show pages, endpoints, and some MVC objects as submodules within the projects to capture a little of their important interactions:

```mermaid
flowchart LR
    subgraph fe["client (react)"]
        direction TB
        InstructorHomePage
        CoordinatorHomePage
        LoginPage

        InstructorHomePage -.-> LoginPage
        CoordinatorHomePage -.-> LoginPage
        LoginPage -.-> InstructorHomePage
        LoginPage -.-> CoordinatorHomePage
    end
    subgraph be["server (Spring Boot)"]
        direction TB
        login
        logout
        UserRepository
        SessionRepository

        login --> UserRepository
        login --> SessionRepository
        logout --> SessionRepository
    end

    subgraph misc
        db[("Data store")]
        gc["Google Cloud Services"]
    end

    LoginPage --> login
    UserRepository --> db
```

