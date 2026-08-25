package com.galileo.ecommerce.user.domain;

import com.galileo.ecommerce.common.domain.BusinessRuleException;
import com.galileo.ecommerce.common.domain.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class UserTest {

    @Test
    void registrationNormalizesEmailAndAssignsCustomerRole() {
        User user = User.register("  John.Doe@Example.COM ", "hash", "John", "Doe");

        assertThat(user.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(user.getRole()).isEqualTo(UserRole.CUSTOMER);
    }

    @Test
    void firstAddressBecomesDefault() {
        User user = customer();
        UUID first = addAddress(user, "Home");
        UUID second = addAddress(user, "Work");

        assertThat(defaultAddressId(user)).isEqualTo(first);
        assertThat(user.getAddresses()).hasSize(2);
        assertThat(second).isNotEqualTo(first);
    }

    @Test
    void markingDefaultSwitchesTheFlag() {
        User user = customer();
        UUID first = addAddress(user, "Home");
        UUID second = addAddress(user, "Work");

        user.markDefaultAddress(second);

        assertThat(defaultAddressId(user)).isEqualTo(second);
        assertThat(user.getAddresses().stream().filter(Address::isDefaultAddress)).hasSize(1);
        assertThat(first).isNotNull();
    }

    @Test
    void removingDefaultPromotesAnotherAddress() {
        User user = customer();
        UUID first = addAddress(user, "Home");
        UUID second = addAddress(user, "Work");

        user.removeAddress(first);

        assertThat(user.getAddresses()).hasSize(1);
        assertThat(defaultAddressId(user)).isEqualTo(second);
    }

    @Test
    void unknownAddressIsReportedAsNotFound() {
        User user = customer();

        assertThatExceptionOfType(ResourceNotFoundException.class)
            .isThrownBy(() -> user.markDefaultAddress(UUID.randomUUID()));
    }

    @Test
    void addressCountIsLimited() {
        User user = customer();
        for (int i = 0; i < 10; i++) {
            addAddress(user, "Address " + i);
        }

        assertThatExceptionOfType(BusinessRuleException.class)
            .isThrownBy(() -> addAddress(user, "One too many"));
    }

    private User customer() {
        return User.register("customer@example.com", "hash", "Jan", "Kowalski");
    }

    private UUID addAddress(User user, String label) {
        return user.addAddress(label, "Main Street 1", "Warsaw", "00-001", "PL");
    }

    private UUID defaultAddressId(User user) {
        return user.getAddresses().stream()
            .filter(Address::isDefaultAddress)
            .findFirst()
            .orElseThrow()
            .getId();
    }
}
