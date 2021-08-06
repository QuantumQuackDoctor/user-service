pipeline {
    agent any
    stages {
        stage('git') {
            steps {
                git branch: 'master', url: 'https://github.com/QuantumQuackDoctor/ORM-Library.git'
                git branch: 'dev', url: 'https://github.com/QuantumQuackDoctor/user-service.git'
            }
        }
        stage('build') {
            steps {
                sh "mvn clean install"
            }
        }
        stage('test') {
            steps {
                sh "mvn clean test"
            }
        }
        stage('sonarqube') {
            steps {
                echo "sonarqube"
                //sh "mvn clean test"
            }
        }
        stage('package') {
            steps {
                echo "package"
                //sh "mvn clean test"
            }
        }
        stage('docker') {
            steps {
                echo "docker"
                //sh "mvn clean test"
            }
        }
        stage('aws') {
            steps {
                echo "aws"
                //sh "mvn clean test"
            }
        }
    }
}