<h1 style="font-weight: bold;">Teach Crafter 💻</h1>

<p>
    <img src="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white" alt="java badge"/>
    <img src="https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white" alt="spring badge"/>
    <img src="https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white" alt="postgres badge"/>
    <img src="https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white" alt="docker badge"/>
    <img src="https://img.shields.io/badge/cloudinary%20%20-8A2BE2?style=for-the-badge&logo=cloudinary&logoColor=white&colorB=blue" alt="cloudinary badge"/>
</p>

<p>
    <a href="#started">Getting Started</a> •
    <a href="#colab">Collaborators</a>
</p>

<p>
    <b>
        Summarizing, The Teach Crafter is a back-end application designed to manage online courses efficiently.
        It provides features for creating and managing courses, user enrollment, handling user authentication, a cron job that runs periodically, 
        and more. The system has been equipped with unit testing to ensure the reliability and performance of its features and
        supports integration with front-end interfaces.
    </b>
</p>

<h2 id="started">🚀 Getting started</h2>

<h3>Prerequisites</h3>

- [JDK 17+](https://www.oracle.com/br/java/technologies/downloads/)
- [Maven](https://maven.apache.org/download.cgi)
- [Docker](https://docs.docker.com/)

<h3>Cloning</h3>

⌨ Clone the repository

```
git clone https://github.com/MiguelSperle/Teach-Crafter.git
```

📂 Access at folder

```
cd Teach-Crafter
```

📡 Install dependencies

```
mvn install
```

<h3>Environment Variables</h3>

```yaml
# DATABASE
DB_URL=YOUR_DATABASE_URL
DB_USERNAME=YOUR_DATABASE_USERNAME
DB_PASSWORD=YOUR_DATABASE_PASSWORD

# JWT
JWT_SECRET=YOUR_JWT_KEY

# JAVA MAIL SENDER
MAIL_USERNAME=YOUR_EMAIL
MAIL_PASSWORD=YOUR_PASSWORD

# CLOUDINARY
CLOUDINARY_CLOUD_NAME=YOUR_CLOUDINARY_CLOUD_NAME
CLOUDINARY_API_KEY=YOUR_CLOUDINARY_API_KEY
CLOUDINARY_API_SECRET=YOUR_CLOUDINARY_API_SECRET
```

<h3>Command to run the container in docker in the background</h3>

```
docker-compose up -d
```

<h3>To see the routes documentation, ensure the application is running</h3>

```
http://localhost:8080/swagger-ui/index.html#/
```

<h2 id="colab">🤝 Collaborator</h2>

<table>
  <tr>
    <td align="center">
      <a href="#">
        <img src="https://avatars.githubusercontent.com/u/102910354?v=4" width="100px;" alt="Miguel Sperle Profile Picture"/><br>
        <sub>
          <b>Miguel Sperle</b>
        </sub>
      </a>
    </td>
  </tr>
</table>