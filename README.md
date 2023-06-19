# FIC Scheduler App

## Abstract 

The FIC Class Scheduler is a course schedule planning tool to help administrative staff develop class schedules which respect the preferences of the teaching staff and maximize their course offerings in the space available. This browser-based application’s primary feature is the generation of a class schedule for the school based on the courses offered, assigning the classes to rooms and professors according to suitability, availability, seniority, and preferences, followed by whatever other constraints are identified. In the proposed version, the application will focus on providing a clear workflow and generating efficient schedules; future work may include features such as tuning or manual tweaking of already-generated schedules, or direct integration of feedback from teachers.

## The Customer: Fraser International College

Fraser International College is a small learning institution located on the Simon Fraser University campus, supporting international students transitioning into degree programs at SFU.
The primary user of our web application will be the FIC administrative staff responsible for creating and managing course schedules. Alongside the admins, the professors at FIC will interact indirectly via a survey which records their availability and teaching preferences.

## Core Problem And Solution

As a small school offering the full range of first year classes for multiple programs, FIC must draw on a pool of part-time teaching staff with very diverse qualifications to teach different classes. This presents a challenging scheduling problem for the administrative staff tasked with drawing up a schedule every semester. Matching staff to classes to rooms is already complex, and further complicating the schedule, part time teachers who are critical to filling out the pool of qualified candidates inevitably have commitments outside FIC.
At Fraser International College, the course and professors scheduling is currently a manual process, managed using spreadsheets. This process is tedious, prone to error, and time-consuming. Checking for double-booked rooms or classes with multiple teachers is not simple. There is an opportunity to automate both the schedule generation and the error-checking afterwards, resulting in better schedules made faster.

## Competitive Analysis

There are a large number of hosted online systems which offer shift scheduling, such as findmyshift (https://www.findmyshift.com/ca/classroom-scheduling), deputy (https://www.deputy.com/industry/education), or Creatrix Campus (https://www.creatrixcampus.com/). Many of these offerings are derived from systems primarily targeting service-industry jobs, and as such focus on shorter-term dynamic scheduling, and features such as time tracking, overtime management, relief coverage for staff who are temporarily away, and so on. These are considerations not immediately relevant to the fixed schedule optimization problem FIC faces. Of course, even a system which is capable of solving FIC’s problems will still need to be configured correctly, and have unneeded features disabled or hidden (if that is possible). We will instead create a directly targeted solution which addresses FIC’s precise needs. Including the fact that we will generate that completely free of charge.


## A Use Case

To get a better sense of the system, we can imagine John, an FIC admin, using the system to plan a semester.
John logs on, ready to plan a new semester. He begins by retrieving a spreadsheet with the preferences and availability of the professors from the survey system. Next, he uploads this spreadsheet into the system, which combines this data with the previously entered list of available courses, and it attempts to produce a trial schedule. If the system is not able to generate a schedule, at this point it produces an error indicating why the course offering can’t be made – because of lack of teachers or rooms, for example. If a schedule is successfully produced, he can export it in a spreadsheet format which is suitable to distribute to the teachers.

## Tech Stack and External Infrastructure

The FIC Class Scheduler will be implemented as a **Spring Boot** web application with a **ReactJS** client side, including external libraries for visualization or validation. The schedule optimization will use the constraint programming capabilities of **Google OR-Tools**, via their **Google Cloud REST** integration. Data will be stored and managed using **PostgreSQ**L.
Source code will be hosted on **GitHu**b, with test automation, deployment, and hosting done via GitHub, **Render.com**, or potentially **Railway.app**.

## Feature Goals (Epics) 

Our project revolves around three main epics: user management and login, course and room setup, and schedule generation.
User management and login includes authentication stories such as login/logout, adding and removing users, and other necessary administrative actions.
Course and room setup includes stories involving the initial setup of the course offerings for a semester, the rooms which can be assigned, any constraints that limit which courses may be assigned to which rooms or at which time slots. This is the one-time (per semester) setup which is independent of the teacher availability.
Schedule generation includes stories involving the input of teacher availability data and other constraints or preferences expressed by the teachers, generating appropriate error messages, formatting the output for display and for distribution, and iterating on or modifying the produced schedule.

## Some of the Mockups for the Login Page and the Main Dashboard Page

### Login
![image](https://github.com/peyz21/FIC-Scheduler/assets/64120482/393b294a-fe49-41ee-b990-a653a877b670)


### Dashboard page 
![image](https://github.com/peyz21/FIC-Scheduler/assets/64120482/8e4b5d5f-3bf9-43d3-956f-9d38b7dec2bf)



