# FIC

Meeting Minutes : meeting 2- talking with the customer and discussing needs, clarifying concerns and points of conflicts 🈺

## Agenda

## Notes

- course scheduling project
- working with FIC, transfer college with sfu, more consistency, smaller class sizes
- Megan sth… head customer

### PROBLEM

- Bunch of courses, bunch of instructors and some of them want to teach different courses
- Input each semester : excel spreadsheet , section of each courses, instructors and their availability, and how many courses they would like to teach.
- We want to match up, the most amount of interests in the courses +
- **GOAL:** get the number of set of courses,
- **Constraints:** the courses cant go at the same time as each other example( macm 101 and cmpt 125 at the same time)  
- **Constraints2:** certain courses require certain rooms, i.e (cmpt 120 would need a lab/ chem/ physics) so they need to be assigned first and they have dedicated rooms
- **Constraints3:** Some instructors would want to back to back days and back to back class sessions, that should be taken into account
- We gonna get: Instructor, the availability day for each, and their preference ( back to back , specific request)
- **Joe’s constraint notes:**
  - hard constraints:
    - physical: one room can only host one course at a time, an instructor can only teach one course at a time
    - room requirements: some courses must be taught in a particular room (or set of rooms?)
    - instructor student capacity: there is a limit to students in a particular course section taught by one instructor (35 students?)
    - instructor availability: an instructor will only be available for certain time slots
    - instructor qualification: an instructor will only be qualified to teach some courses
  - soft constraints:
    - some classes shouldn't be run in the same timeslot in different rooms, because they are commonly taken by the same students in the same semester (especially if they’re both required by a particular program)
    - duress availability: some instructors may prefer to avoid particular slots, but if there is no other way to schedule course, they may be willing to work then
    - back-to-back scheduling: some instructors prefer to have assignments on either the same day or on consecutive days
    - minimum separation: some instructors may prefer not to have different teaching timeslots assigned that are consecutive without a break?
- The form is collected using <https://www.machform.com> so they might be using Google sheets internally – should verify their internal data format, if Excel is just an export format then we should figure out what is easiest for them/us as an intermediate step
- We should have some kind of login/user management, but it’s not the most important thing. If they use federated login we could maybe hook into it, or we could make our own thing.

## Action Items

- Getting a schedule out the first PHASE,
- Get proposals in
- Talk through the iteration
- Each iteration have a video demo 1-2 days before of the iteration due date, explaining features, and points

## Questions

- Is there a maximum number of courses an instructor can teach, independent of availability?
- Is there a fairness or work balancing consideration, where we want to make sure each instructor gets some work?
- Sometimes a bidding system is used to resolve conflicts between external entities, is that possibly desired here?
- Do we want to integrate the manual notes into our system?
