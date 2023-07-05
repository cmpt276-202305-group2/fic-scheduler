# Tech Stack and External Infrastructure

The FIC Class Scheduler is implemented as a Spring Boot web application with a ReactJS client side, including external libraries for visualization or validation. The schedule optimization will use the constraint programming capabilities of Google OR-Tools, via their Google Cloud REST integration. Data is stored and managed using PostgreSQL (and H2, for testing).

Source code is hosted on GitHub, with test automation, deployment, and hosting done via GitHub and Render.com.
