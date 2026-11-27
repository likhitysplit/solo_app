package io.github.likhitysplit.solo;

import io.github.likhitysplit.solo.user.User;
import io.github.likhitysplit.solo.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class SoloApplicationTests {

	@Autowired
	private UserRepository users;

	@Test
	void savesAndFindsUser() {
		User user = new User("likhi", "likhi@example.com", "test-hash", "America/New_York");

		User saved = users.save(user);

		assertThat(saved.getId()).isNotNull();

		User found = users.findByEmail("likhi@example.com").orElseThrow();
		assertThat(found.getUsername()).isEqualTo("likhi");
		assertThat(found).isSameAs(saved);

		assertThat(users.existsByUsername("likhi")).isTrue();
	}
}