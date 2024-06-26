# Contributing
Thanks for considering taking the time to contribute to Holocore! :heart:

## Setting up a development environment
This section will guide you through setting up a development environment for Holocore.

### Docker
The project uses [Docker](https://www.docker.com) to make it easy to run a MongoDB instance.

You can download Docker [here](https://www.docker.com/get-started).

If you see the error "Could not find a valid Docker environment" when running automated tests on Linux, you may additionally need to do the following for them to work:

```bash
$ sudo groupadd docker
$ sudo gpasswd -a $USER docker
$ sudo service docker restart
$ sudo chmod 666 /var/run/docker.sock
```

### IDE
We recommend using [IntelliJ IDEA](https://www.jetbrains.com/idea/download) as your IDE. It's free and has great support for Gradle projects.

It will also automatically use the project's code style settings and add license headers to relevant files.
This greatly reduces the amount of friction when contributing.

### Java Development Kit
Assuming you are using IntelliJ IDEA, you can easily install the correct JDK version by opening the project in IntelliJ IDEA and clicking on the "Install JDK" link in the top right corner.

If you already have a JDK installed, but perhaps not the correct version, follow [this guide](https://www.jetbrains.com/help/idea/sdk.html#define-sdk) to download a JDK.

### Git submodules
The project uses [git submodules](https://git-scm.com/book/en/v2/Git-Tools-Submodules).

Clone them by running: `git submodule update --init`.

You may have to manually update the submodules in the future, if they become outdated: `git submodule update`.

### MongoDB
A MongoDB instance can be bootstrapped by running `docker-compose up -d`.

This will run and initialize a MongoDB instance with a default game server user.
* Username: user
* Password: pass

This will also run a web UI, which can be accessed in the browser at: http://localhost:8081

You can stop both with: `docker-compose down`.

### Running Holocore
In IntelliJ idea, run the **Start** run configuration.

**NOTE**: Docker must be running in the background for MongoDB to start properly.

### Running automated tests
In IntelliJ idea, run the **Tests** run configuration.

## Your first contribution
If you're new to contributing to open source projects, we recommend reading [this guide](https://opensource.guide/how-to-contribute/).

Some issues require coding, while others are more focused on documentation or other non-coding tasks.

### Good first issues
If you're new to the project, you can start by looking at the [good first issues](https://github.com/ProjectSWGCore/Holocore/issues?q=is%3Aopen+is%3Aissue+label%3A%22Good+first+issue%22).

These are issues that are relatively easy to fix, and are intended for new contributors to get started with.
We will try to provide as much information as possible in the issue description, but don't hesitate to ask for more information if you need it.

We're happy to help you get started with your first contribution, so don't hesitate to ask for help.
A good place to ask questions is on our Discord server, in the #development channel.

[![Discord chat](https://img.shields.io/discord/373548910225915905?logo=discord)](https://discord.gg/BWhBx4F)

Once you have completed one or two Good first issues, you can start looking at other issues.
These may be more complex, but we're happy to help you get started.
The issue description is likely to be less detailed, so you may have to ask for more information.

### Programming languages
The project is written in Java and Kotlin, and uses [Gradle](https://gradle.org) as its build system.

We are in the process of transitioning from Java to Kotlin, so some parts of the codebase are still written in Java.

To make that transition as easy as possible, we recommend you **write new code in Kotlin**.
If you have to edit existing Java code, you may use Java.

If we find ourselves editing a piece of Java code often, we should consider rewriting it in Kotlin. This would be a separate task, and should be discussed in an issue.

### Creating a pull request
When you're ready to submit your changes, you can create a pull request.

A pull request is a way to propose your changes to the project. It allows other contributors to review the changes, and suggest improvements.

To create a pull request, follow [this guide](https://help.github.com/en/github/collaborating-with-issues-and-pull-requests/creating-a-pull-request).

### Code review
Once you've created a pull request, it will be reviewed by other contributors.

If there are any issues or suggestions for improvements, you can simply push new commits, and they will automatically be added to the pull request.

Once the pull request has been approved, it will be merged into the `master` branch.

## Reporting bugs
If you've found a bug, please report it as an issue on the [issues page](https://github.com/ProjectSWGCore/Holocore/issues).

Please include as much information as possible, such as:
* Steps to reproduce the bug
* What you expected to happen
* What actually happened
* Screenshots, if applicable
* Server logs, if applicable
