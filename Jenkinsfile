pipeline {
    agent any
    stages {
        stage('git') {
            steps {
                git branch: 'dev', url: 'https://github.com/QuantumQuackDoctor/user-service.git'
            }
        }
        stage('remove') {
            steps {
                sh "rm -rf /var/lib/jenkins/.m2/repository/"
            }
        }
        stage('build') {
            steps {
                sh "mvn clean install -X -Dspring.datasource.url=jdbc:postgresql://localhost:5432/test"
            }
        }
    }
}