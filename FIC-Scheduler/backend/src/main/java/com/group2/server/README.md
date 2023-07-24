Some useful snippets:

```
private static final Logger logger = LoggerFactory.getLogger(CourseOfferingController.class);
```

```
// thingies thingy thingyDto thingyDtoList thingyRepository Thingy ThingyDto
@GetMapping("/thingies")
public ResponseEntity<List<ThingyDto>> readListByQuery() {
    try {
        return new ResponseEntity<>(thingyRepository.findAll().stream().map(this::toDto).toList(),
                HttpStatus.OK);
    } catch (Exception e) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}

@GetMapping("/thingies/{id}")
public ResponseEntity<ThingyDto> readOneById(@PathVariable Integer id) {
    try {
        return new ResponseEntity<>(toDto(thingyRepository.findById(id).get()),
                HttpStatus.OK);
    } catch (Exception e) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}

@PostMapping("/thingies")
public ResponseEntity<List<ThingyDto>> createOrUpdateList(@RequestBody List<ThingyDto> thingyDtoList) {
    try {
        return new ResponseEntity<>(
                thingyDtoList.stream().map(this::createOrUpdateFromDto).map(this::toDto).toList(),
                HttpStatus.OK);
    } catch (Exception e) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}

@PutMapping("/thingies/{id}")
public ResponseEntity<ThingyDto> updateOneById(@PathVariable Integer id,
        @RequestBody ThingyDto thingyDto) {
    try {
        if ((thingyDto.getId() != null) && !id.equals(thingyDto.getId())) {
            throw new IllegalArgumentException();
        }
        thingyDto.setId(id);
        return new ResponseEntity<>(toDto(createOrUpdateFromDto(thingyDto)),
                HttpStatus.OK);
    } catch (Exception e) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}

@DeleteMapping("/thingies/{id}")
public ResponseEntity<Void> deleteOneById(@PathVariable Integer id) {
    try {
        thingyRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    } catch (Exception e) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}

public ThingyDto toDto(Thingy thingy) {
    return new ThingyDto(thingy.getId());
}

public Thingy createOrUpdateFromDto(ThingyDto thingyDto) {
    Thingy thingy;
    if (thingyDto.getId() != null) {
        thingy = thingyRepository.findById(thingyDto.getId()).get();
    } else {
        thingy = new Thingy(null, ...);
    }
    return thingy;
}
```

To prevent infinite recursion, you can break data back-reference loops with
`@JsonIgnore` (fasterxml) and `@ToString.Exclude` (lombok).
