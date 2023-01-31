# Food Ordering System :: DDD

![GitHub language count](https://img.shields.io/github/languages/count/tacsio/food-system-ddd?color=%2331acbf)
![GitHub top language](https://img.shields.io/github/languages/top/tacsio/food-system-ddd?color=%2331acbf)
![GitHub last commit](https://img.shields.io/github/last-commit/tacsio/food-system-ddd?color=%2331acbf)
[![GitHub issues](https://img.shields.io/github/issues-raw/tacsio/food-system-ddd?color=%2331acbf)](https://github.com/tacsio/food-system-ddd/issues)
[![GitHub commit activity](https://img.shields.io/github/commit-activity/w/tacsio/food-system-ddd?color=%2331acbf)](https://github.com/tacsio/food-system-ddd/graphs/commit-activity)
![Maintenance](https://img.shields.io/maintenance/yes/2023?color=%2331acbf)

## :package: System Architecture

![arch](.assets/food-system-arch.png)

#### :bangbang: Attention

> **order-omain-core** should not have any dependencies. 
> That component should be the most stable component in the system
> since it have the business logic.

> **order-application-service** should not have either any external dependencies.

### :wrench: Generating Modules Architecture with Maven Depgraph plugin

> Depends on graphviz

```bash
mvn com.github.ferstl:depgraph-maven-plugin:aggregate -DcreateImage=true -DclasspathScope=compile -DshowGroupIds -DshowVersions "-Dincludes=com.food.ordering.system*:*"
```

### :package: Order Service Domain

![order-service](.assets/order-service-domain-logic.png)