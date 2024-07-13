package com.miguelsperle.teach_crafter.utils.integration.configuration;

import com.github.javafaker.Faker;
import lombok.Getter;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collection;

public class H2CleanUpAndFakerExtension implements BeforeEachCallback {
    @Getter
    private static Faker faker;

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        final var repositories = SpringExtension
                .getApplicationContext(extensionContext)
                .getBeansOfType(CrudRepository.class)
                .values();

        cleanUp(repositories);

        faker = new Faker();
    }

    private void cleanUp(final Collection<CrudRepository> repositories) {
        repositories.forEach(CrudRepository::deleteAll);
    }
}