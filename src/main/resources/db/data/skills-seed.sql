INSERT INTO specialization (direction, language)
VALUES
    ('BACKEND', 'JAVA'), ('BACKEND', 'PYTHON'), ('BACKEND', 'CSHARP'), ('BACKEND', 'KOTLIN'),
    ('BACKEND', 'GO'), ('BACKEND', 'PHP'), ('BACKEND', 'RUBY'), ('BACKEND', 'RUST'),
    ('BACKEND', 'JAVASCRIPT'), ('BACKEND', 'TYPESCRIPT'), ('FRONTEND', 'JAVASCRIPT'),
    ('FRONTEND', 'TYPESCRIPT'), ('FRONTEND', 'PYTHON'), ('FULLSTACK', 'JAVA'),
    ('FULLSTACK', 'JAVASCRIPT'), ('FULLSTACK', 'TYPESCRIPT'), ('FULLSTACK', 'PYTHON'),
    ('FULLSTACK', 'CSHARP'), ('FULLSTACK', 'PHP'), ('FULLSTACK', 'RUBY'),
    ('MOBILE', 'KOTLIN'), ('MOBILE', 'JAVA'), ('MOBILE', 'SWIFT'), ('DATA', 'PYTHON'),
    ('DATA', 'SQL'), ('DATA', 'JAVA'), ('DEVOPS', 'CLOUD'), ('DEVOPS', 'GO'),
    ('DEVOPS', 'PYTHON'), ('QA', 'JAVA'), ('QA', 'JAVASCRIPT'), ('QA', 'PYTHON')
    ON CONFLICT DO NOTHING;

INSERT INTO skill (name)
VALUES
    ('Java'), ('Spring'), ('Spring Boot'), ('Spring Security'), ('Spring Data JPA'),
    ('Hibernate'), ('JPA'), ('Maven'), ('Gradle'), ('JUnit'), ('Mockito'),
    ('Kafka'), ('RabbitMQ'), ('PostgreSQL'), ('MySQL'), ('Redis'), ('MongoDB'),
    ('Elasticsearch'), ('SQL'), ('REST API'), ('GraphQL'), ('HTML'), ('CSS'),
    ('JavaScript'), ('TypeScript'), ('React'), ('Redux'), ('Angular'), ('Vue'),
    ('Node.js'), ('Express.js'), ('NestJS'), ('Next.js'), ('Python'), ('Django'),
    ('FastAPI'), ('Flask'), ('Pandas'), ('NumPy'), ('C#'), ('ASP.NET'),
    ('Entity Framework'), ('Kotlin'), ('Android'), ('Swift'), ('iOS'), ('Go'),
    ('Gin'), ('C'), ('C++'), ('Rust'), ('PHP'), ('Laravel'), ('Ruby'),
    ('Ruby on Rails'), ('Docker'), ('Kubernetes'), ('AWS'), ('Azure'),
    ('Google Cloud'), ('Terraform'), ('Ansible'), ('Jenkins'), ('GitHub Actions'),
    ('GitLab CI'), ('Linux'), ('Bash'), ('Nginx'), ('Apache'), ('Prometheus'),
    ('Grafana'), ('OpenAPI'), ('Swagger'), ('Git')
    ON CONFLICT (name) DO NOTHING;

INSERT INTO skill_dictionary (skill_id, import_pattern, quick_signals)
VALUES
    ((SELECT id FROM skill WHERE name = 'Docker'), 'filename:Dockerfile', 'docker-compose,container'),
    ((SELECT id FROM skill WHERE name = 'Kubernetes'), 'filename:deployment.yaml', 'k8s,helm,kubectl'),
    ((SELECT id FROM skill WHERE name = 'Jenkins'), 'filename:Jenkinsfile', 'ci-cd'),
    ((SELECT id FROM skill WHERE name = 'GitHub Actions'), 'path:.github/workflows', 'github-actions,yaml'),
    ((SELECT id FROM skill WHERE name = 'GitLab CI'), 'filename:.gitlab-ci.yml', 'gitlab-ci'),
    ((SELECT id FROM skill WHERE name = 'Terraform'), 'extension:tf', 'hcl,iac'),
    ((SELECT id FROM skill WHERE name = 'Ansible'), 'filename:playbook.yml', 'ansible,yaml'),

    ((SELECT id FROM skill WHERE name = 'Maven'), 'filename:pom.xml', 'mvn,dependency'),
    ((SELECT id FROM skill WHERE name = 'Gradle'), 'filename:build.gradle', 'gradle.kts,groovy'),

    ((SELECT id FROM skill WHERE name = 'Kafka'), 'kafka', 'spring-kafka,bootstrap-servers'),
    ((SELECT id FROM skill WHERE name = 'Redis'), 'redis', 'spring-data-redis,jedis,lettuce'),
    ((SELECT id FROM skill WHERE name = 'PostgreSQL'), 'postgresql', 'postgres,psql,jdbc:postgresql'),
    ((SELECT id FROM skill WHERE name = 'MongoDB'), 'mongodb', 'mongo,nosql'),
    ((SELECT id FROM skill WHERE name = 'Elasticsearch'), 'elasticsearch', 'elk,search-engine'),
    ((SELECT id FROM skill WHERE name = 'RabbitMQ'), 'rabbitmq', 'amqp,spring-rabbit'),

    ((SELECT id FROM skill WHERE name = 'Java'), 'extension:java', 'jdk,jre'),
    ((SELECT id FROM skill WHERE name = 'Spring Boot'), 'SpringBootApplication', 'spring-boot,starter'),
    ((SELECT id FROM skill WHERE name = 'Spring Security'), 'EnableWebSecurity', 'security,auth,jwt'),
    ((SELECT id FROM skill WHERE name = 'Spring Data JPA'), 'JpaRepository', 'repository,datasource'),
    ((SELECT id FROM skill WHERE name = 'Hibernate'), 'hibernate', 'entity,orm'),
    ((SELECT id FROM skill WHERE name = 'JUnit'), 'Test', 'junit,test,assert'),
    ((SELECT id FROM skill WHERE name = 'Mockito'), 'Mockito', 'mock,stub'),
    ((SELECT id FROM skill WHERE name = 'REST API'), 'RestController', 'mapping,endpoint'),

    ((SELECT id FROM skill WHERE name = 'React'), 'extension:jsx', 'react,tsx,hooks'),
    ((SELECT id FROM skill WHERE name = 'TypeScript'), 'extension:ts', 'ts'),
    ((SELECT id FROM skill WHERE name = 'HTML'), 'extension:html', 'web,frontend'),
    ((SELECT id FROM skill WHERE name = 'CSS'), 'extension:css', 'style'),

    ((SELECT id FROM skill WHERE name = 'Python'), 'extension:py', 'pip,import'),
    ((SELECT id FROM skill WHERE name = 'Go'), 'extension:go', 'golang'),
    ((SELECT id FROM skill WHERE name = 'Rust'), 'filename:Cargo.toml', 'rust-lang'),
    ((SELECT id FROM skill WHERE name = 'C++'), 'extension:cpp', 'iostream'),
    ((SELECT id FROM skill WHERE name = 'C#'), 'extension:cs', 'dotnet,csharp'),
    ((SELECT id FROM skill WHERE name = 'Swift'), 'extension:swift', 'ios,apple'),

    ((SELECT id FROM skill WHERE name = 'AWS'), 'amazonaws', 's3,ec2,cloud'),
    ((SELECT id FROM skill WHERE name = 'Nginx'), 'filename:nginx.conf', 'proxy,load-balancer'),
    ((SELECT id FROM skill WHERE name = 'Prometheus'), 'prometheus.yml', 'monitoring'),
    ((SELECT id FROM skill WHERE name = 'Grafana'), 'grafana', 'dashboard'),
    ((SELECT id FROM skill WHERE name = 'Git'), 'filename:.gitignore', 'vcs')
    ON CONFLICT (skill_id) DO NOTHING;