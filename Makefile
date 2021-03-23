branch:=$(shell git rev-parse --abbrev-ref HEAD)

release:
	if [[ "$(branch)" == "master" ]]; then \
		echo "Creating new release..."; \
		./mvnw clean build-helper:parse-version release:clean release:prepare release:perform \
		  -P release --batch-mode \
		  -DdevelopmentVersion='$${parsedVersion.majorVersion}.$${parsedVersion.nextMinorVersion}.0-SNAPSHOT' \
		  -Darguments="-Dmaven.deploy.skip=true -Dmaven.javadoc.skip=true -DskipTests"; \
	else \
		echo "Ensure that you're working on master when doing a release."; \
	fi

