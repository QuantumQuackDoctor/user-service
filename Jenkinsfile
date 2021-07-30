pipeline {
    agent any
    stages {
        stage('---clean---') {
            steps {
                sh "mvn clean install -X -Dspring.datasource.url=jdbc:postgresql://localhost:5432/test"
            }
        }
    }
}