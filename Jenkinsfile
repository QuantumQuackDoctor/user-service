pipeline {
    agent any
    stages {
        stage('---clean---') {
            steps {
                sh "mvn clean install -Dspring.datasource.url=jdbc:postgresql://localhost:5432/test"
            }
        }
        stage('--test--') {
            steps {
                sh "mvn test"
            }
        }
        stage('--package--') {
            steps {
                sh "mvn package"
            }
        }
    }
}