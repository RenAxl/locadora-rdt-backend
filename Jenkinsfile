pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
        skipDefaultCheckout(true)
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    triggers {
        githubPush()
    }

    environment {
        APP_NAME = 'locadora-rdt-backend'
        PROD_IMAGE_TAG = "prod-${env.BUILD_NUMBER}"
        COMPOSE_PROJECT_DIR = '/workspace/locadora-rdt/locadora-rdt-devops'
    }

    stages {
        stage('Checkout') {
            when {
                anyOf {
                    branch 'dev'
                    branch 'main'
                }
            }
            steps {
                checkout scm
                sh 'git status --short --branch'
            }
        }

        stage('Maven Clean Package') {
            when {
                anyOf {
                    branch 'dev'
                    branch 'main'
                }
            }
            agent {
                docker {
                    image 'maven:3.8.8-eclipse-temurin-11'
                    args '-v maven_repository:/root/.m2'
                    reuseNode true
                }
            }
            steps {
                sh 'chmod +x ./mvnw'
                sh './mvnw clean package'
            }
            post {
                always {
                    junit allowEmptyResults: false, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Testes Automatizados') {
            when {
                branch 'dev'
            }
            agent {
                docker {
                    image 'maven:3.8.8-eclipse-temurin-11'
                    args '-v maven_repository:/root/.m2'
                    reuseNode true
                }
            }
            steps {
                sh 'chmod +x ./mvnw'
                sh './mvnw verify'
            }
            post {
                always {
                    junit allowEmptyResults: false, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube') {
            when {
                branch 'dev'
            }
            agent {
                docker {
                    image 'maven:3.9.9-eclipse-temurin-17'
                    args '-v maven_repository:/root/.m2 --network locadora-rdt-network'
                    reuseNode true
                }
            }
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'chmod +x ./mvnw'
                    sh './mvnw sonar:sonar -Dsonar.projectKey=locadora-rdt-backend -Dsonar.projectName=locadora-rdt-backend -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml'
                }
            }
        }

        stage('Quality Gate - Locadora RDT') {
            when {
                branch 'dev'
            }
            agent {
                docker {
                    image 'curlimages/curl:8.11.1'
                    args '--network locadora-rdt-network'
                    reuseNode true
                }
            }
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh '''
                        set -eu

                        if [ ! -f target/sonar/report-task.txt ]; then
                          echo "Arquivo target/sonar/report-task.txt nao encontrado. A analise SonarQube nao foi executada corretamente."
                          exit 1
                        fi

                        ce_task_url="$(grep '^ceTaskUrl=' target/sonar/report-task.txt | cut -d= -f2-)"
                        project_key="$(grep '^projectKey=' target/sonar/report-task.txt | cut -d= -f2- || true)"

                        if [ -z "${project_key}" ]; then
                          project_key="locadora-rdt-backend"
                        fi

                        auth_args=""
                        if [ -n "${SONAR_AUTH_TOKEN:-}" ]; then
                          auth_args="-u ${SONAR_AUTH_TOKEN}:"
                        fi

                        analysis_id=""

                        for attempt in $(seq 1 60); do
                          ce_response="$(curl -fsS ${auth_args} "${ce_task_url}")"
                          ce_status="$(printf '%s' "${ce_response}" | sed -n 's/.*"status":"\\([^"]*\\)".*/\\1/p')"

                          echo "SonarQube Compute Engine status: ${ce_status} (${attempt}/60)"

                          if [ "${ce_status}" = "SUCCESS" ]; then
                            analysis_id="$(printf '%s' "${ce_response}" | sed -n 's/.*"analysisId":"\\([^"]*\\)".*/\\1/p')"
                            break
                          fi

                          if [ "${ce_status}" = "FAILED" ] || [ "${ce_status}" = "CANCELED" ]; then
                            echo "Processamento da analise SonarQube falhou: ${ce_status}"
                            exit 1
                          fi

                          sleep 5
                        done

                        if [ -z "${analysis_id}" ]; then
                          echo "Timeout aguardando processamento da analise SonarQube."
                          exit 1
                        fi

                        quality_gate_failed=0

                        measures_response="$(curl -fsS ${auth_args} "${SONAR_HOST_URL}/api/measures/component?component=${project_key}&metricKeys=new_bugs,new_vulnerabilities,new_security_hotspots,new_security_hotspots_reviewed,new_coverage,new_duplicated_lines_density,new_reliability_rating,new_software_quality_reliability_rating")"

                        get_measure() {
                          metric_key="$1"
                          printf '%s' "${measures_response}" \
                            | grep -o "\"metric\":\"${metric_key}\"[^}]*}[^}]*}" \
                            | grep -o '"value":"[^"]*"' \
                            | cut -d '"' -f 4 \
                            | head -n 1
                        }

                        check_number_equals() {
                          metric_key="$1"
                          label="$2"
                          expected="$3"
                          actual_value="$(get_measure "${metric_key}" || true)"

                          if [ -z "${actual_value}" ] && [ "${expected}" = "0" ]; then
                            actual_value="0"
                          fi

                          echo "${label}: ${actual_value:-N/A} (esperado: ${expected})"

                          if [ -z "${actual_value}" ] || ! awk "BEGIN { exit !(${actual_value} == ${expected}) }"; then
                            quality_gate_failed=1
                          fi
                        }

                        check_number_at_least() {
                          metric_key="$1"
                          label="$2"
                          expected="$3"
                          actual_value="$(get_measure "${metric_key}" || true)"

                          echo "${label}: ${actual_value:-N/A} (esperado: >= ${expected})"

                          if [ -z "${actual_value}" ] || ! awk "BEGIN { exit !(${actual_value} >= ${expected}) }"; then
                            quality_gate_failed=1
                          fi
                        }

                        check_number_at_most() {
                          metric_key="$1"
                          label="$2"
                          expected="$3"
                          actual_value="$(get_measure "${metric_key}" || true)"

                          echo "${label}: ${actual_value:-N/A} (esperado: <= ${expected})"

                          if [ -z "${actual_value}" ] || ! awk "BEGIN { exit !(${actual_value} <= ${expected}) }"; then
                            quality_gate_failed=1
                          fi
                        }

                        check_security_hotspots_reviewed() {
                          reviewed_value="$(get_measure "new_security_hotspots_reviewed" || true)"
                          hotspots_value="$(get_measure "new_security_hotspots" || true)"

                          if [ -z "${hotspots_value}" ]; then
                            hotspots_value="0"
                          fi

                          if [ -z "${reviewed_value}" ] && awk "BEGIN { exit !(${hotspots_value} == 0) }"; then
                            reviewed_value="100"
                          fi

                          echo "Security Hotspots Reviewed on New Code: ${reviewed_value:-N/A} (esperado: >= 100)"

                          if [ -z "${reviewed_value}" ] || ! awk "BEGIN { exit !(${reviewed_value} >= 100) }"; then
                            quality_gate_failed=1
                          fi
                        }

                        check_reliability_rating() {
                          actual_value="$(get_measure "new_reliability_rating" || true)"

                          if [ -z "${actual_value}" ]; then
                            actual_value="$(get_measure "new_software_quality_reliability_rating" || true)"
                          fi

                          echo "Reliability Rating on New Code: ${actual_value:-N/A} (esperado: A)"

                          if [ -z "${actual_value}" ] || ! awk "BEGIN { exit !(${actual_value} <= 1) }"; then
                            quality_gate_failed=1
                          fi
                        }

                        check_number_equals "new_bugs" "New Bugs" "0"
                        check_number_equals "new_vulnerabilities" "New Vulnerabilities" "0"
                        check_security_hotspots_reviewed
                        check_number_at_least "new_coverage" "Coverage on New Code" "80"
                        check_number_at_most "new_duplicated_lines_density" "Duplicated Lines on New Code" "3"
                        check_reliability_rating

                        if [ "${quality_gate_failed}" -ne 0 ]; then
                          echo "Quality Gate - Locadora RDT reprovado para metricas de New Code."
                          printf '%s\\n' "${measures_response}"
                          exit 1
                        fi

                        echo "Quality Gate - Locadora RDT aprovado para metricas de New Code."
                    '''
                }
            }
        }

        stage('Archive JAR') {
            when {
                branch 'main'
            }
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Docker Build - Main') {
            when {
                branch 'main'
            }
            steps {
                sh '''
                    docker build \
                      -t ${APP_NAME}:${PROD_IMAGE_TAG} \
                      -t ${APP_NAME}:prod-latest \
                      -t ${APP_NAME}:latest \
                      .
                '''
            }
        }

        stage('Deploy Local Backend') {
            when {
                branch 'main'
            }
            steps {
                sh '''
                    IMAGE_TAG=${PROD_IMAGE_TAG} docker compose \
                      -f ${COMPOSE_PROJECT_DIR}/docker-compose.yml \
                      up -d --no-build backend
                '''
            }
        }

        stage('Backend Health Check') {
            when {
                branch 'main'
            }
            steps {
                sh '''
                    for attempt in $(seq 1 30); do
                      if docker run --rm --network locadora-rdt-network curlimages/curl:8.11.1 \
                        -fsS http://backend:8080/actuator/health; then
                        exit 0
                      fi

                      echo "Backend ainda nao esta pronto. Tentativa ${attempt}/30"
                      sleep 5
                    done

                    echo "Backend nao respondeu com sucesso no health check."
                    docker logs --tail 120 locadora-rdt-backend || true
                    exit 1
                '''
            }
        }
    }

    post {
        failure {
            echo 'Pipeline FAILED. Etapas posteriores nao serao executadas apos falha anterior.'
        }
        success {
            echo 'Pipeline SUCCESS.'
        }
        always {
            cleanWs()
        }
    }
}
