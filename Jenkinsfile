pipeline {
    agent any
    environment {
        env.JAVA_HOME="${tool 'java-1.8.0-openjdk-1.8.0.282.b08-1.amzn2.0.1.x86_64'}"
        env.PATH="${env.JAVA_HOME}/bin:${env.PATH}"
    }
    stages {
        stage('git') {
            steps {
                git branch: 'dev', url: 'https://github.com/QuantumQuackDoctor/user-service.git'
            }
        }
        stage('remove') {
            steps {
                sh "rm -rf rm -rf ~/.m2/repository/org/apache/"
            }
        }
        stage('build') {
            steps {
                sh "mvn clean install -X -Dspring.datasource.url=jdbc:postgresql://localhost:5432/test"
            }
        }
    }
}