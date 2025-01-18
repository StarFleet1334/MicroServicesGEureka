# Client side microservice

## Documentation
* Run main class WorkloadApplication

# Index
* Since there is only one custom Index by searching firstName and LastName
* This command can be used in mongoDB atlas: db.trainers.explain("executionStats").find({ firstName: "John", lastName: "Doe" });
* Since I used CompoundIndex, Spring Data will automatically use it for query matches that matched index fields
* If you want to curl without usage of MongoDb Compass you can use: http://localhost:8081/trainers/search?firstName=#{firstName}&lastName=#{lastName} 



# Run Cucumber Tests
* Navigate to folder: cd workload
* To run JMS cucumber test: gradle clean test --tests "com.micro.workload.jms.JmsCucumberTest"
* To run Trainer cucumber test: gradle clean test --tests "com.micro.workload.trainerComponentTest.TrainerCucumberTest"
* To run Workload cucumber test: gradle clean test --tests "com.micro.workload.workloadComponentTest.WorkloadCucumberTest"
* To run Integration cucumber test, first navigate into Main repository's README.md on how to run everything
* and then run using: gradle clean test --tests "com.micro.workload.integration.IntegrationTestRunner" or you can run manually using mouse click on class


## API Endpoints

#### Get Trainer's yearly/monthly duration summary

```http
  GET /trainers/
```

| PathVariable | Type     | Description                |
|:-------------| :------- | :------------------------- |
| `username`   | `string` | **Required**. |


#### Get All Trainer's

```http
  GET /trainers/
```
