# Dynamic Jenkins Pipeline Plugin

The goal of this pipeline is to provide a very simple abstraction over top of Jenkins pipeline so that users can create powerful, dynamic pipelines without having to have any programming skills. This will be done by:

1. providing a declarative data model for environment promotion. this work is currently being done [here](https://github.com/rht-labs/api-design)
2. generating Jenkinsfile from the data model with the code in this project