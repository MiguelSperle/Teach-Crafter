<h1 style="font-weight: bold;">Teach Crafter 💻</h1>

<p>
    <img src="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white" alt="java badge"/>
    <img src="https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white" alt="spring badge"/>
    <img src="https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white" alt="postgres badge"/>
    <img src="https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white" alt="docker badge"/>
    <img src="https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white" alt="maven badge"/>
</p>

<p>
    <a href="#started">Getting Started</a> • 
    <a href="#routes">API Endpoints</a> •
    <a href="#colab">Collaborators</a>
</p>

<p>
    <b>
        Summarizing, The Teach Crafter is a back-end application designed to manage online courses efficiently.
        It provides features for creating and managing courses, enrolling students, handling user authentication and more.
        The system supports integration with front-end interfaces.
    </b>
</p>

<h2 id="started">🚀 Getting started</h2>

<h3>Prerequisites</h3>

- [Java 17+](https://www.oracle.com/br/java/technologies/downloads/)
- [Maven](https://maven.apache.org/download.cgi)
- [Docker](https://docs.docker.com/)
- [Postgres](https://hub.docker.com/_/postgres)

<h3>Cloning</h3>

```⌨ Clone the repository```

```
git clone https://github.com/MiguelSperle/Teach-Crafter.git
```

```📂 Access at folder```

```
cd Teach-Crafter
```

```📡 Install dependencies```

```
mvn install
```

<h3>Environment Variables</h3>

```yaml
spring.datasource.url={YOUR_DATABASE_URL}
spring.datasource.username={YOUR_DATABASE_USERNAME}
spring.datasource.password={YOUR_DATABASE_PASSWORD}

spring.mail.username={YOUR_EMAIL}
spring.mail.password={YOUR_PASSWORD}

spring.cloudinary.cloud_name={YOUR_CLOUDINARY_CLOUD_NAME}
spring.cloudinary.api_key={YOUR_CLOUDINARY_API_KEY}
spring.cloudinary.api_secret={YOUR_CLOUDINARY_API_SECRET}
```
