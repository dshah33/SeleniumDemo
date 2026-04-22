# Decision Log -- AAA Life SDET Technical Assessment

## Scope Interpretation and Timeboxing

The assessment targets [saucedemo.com](https://www.saucedemo.com), a static demo e-commerce app with a fixed product
catalog and predetermined user accounts. The goal is to demonstrate framework design, test strategy, and automation
skill -- not to exhaustively cover every edge case on a demo site. Work was time boxed to produce a clean, runnable
framework with meaningful coverage rather than a sprawling test suite.

## Test Selection and Coverage Rationale

Three tests were chosen to cover the two highest-value areas:

**Login (2 tests)** -- The authentication gate. One happy-path test confirms standard login reaches the dashboard; one
negative test confirms the locked-out user receives the correct error. These two cases cover the critical success path
and the most common failure mode, and validate that the framework correctly handles both outcomes.

**End-to-end checkout (1 test)** -- A single test that exercises the full purchase funnel: login, add items, verify
cart, enter shipping details, review order totals, and confirm success. Rather than splitting this into many small tests
with shared state, it runs as one flow because:

- It mirrors real user behavior (a single session from login to order confirmation).
- It validates **price integrity** at three independent checkpoints (dash board, cart, order review), catching
  calculation bugs that isolated tests would miss.
- It keeps test data self-contained -- no fixtures or shared cart state to manage.

Together, the three tests cover authentication, catalog interaction, cart management, form entry, and order
completion -- the core surface area of the application.

## Stability and Data Strategies

| Concern             | Approach                                                                                                                                                                                                          |
|---------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Flaky locators**  | CSS selectors use `data-test` attributes and stable class names rather than fragile XPath or positional selectors.                                                                                                |
| **Timing**          | Configurable implicit + explicit waits. No hard-coded `Thread.sleep()` calls anywhere.                                                                                                                            |
| **Parallel safety** | `ThreadLocal<WebDriver>` ensures each test thread has its own browser; no shared mutable state.                                                                                                                   |
| **Test data**       | `DataGenerator` produces random names and ZIP codes per run, avoiding collisions if tests are ever run concurrently against a shared backend. Credentials come from `config.properties`, not hard-coded in tests. |

| **Headless CI**     | Headless mode is on by default, so the suite runs cleanly in CI environments with no display server.                                                                                                              |

## Project Structure Decisions

- **Page Object Model** -- Each page is a class with locators and actions. Tests read like a script (
  `loginPage.loginAs(...)`), and locator changes are isolated to one file.
- **Fluent API** -- Page methods return `this` or the next page object, enabling chained calls that are concise and
  self-documenting.
- **Framework vs. tests separation** -- `com.framework.*` (driver management, config, listeners, utilities) is fully
  decoupled from `com.demo.*` (pages, tests). The framework could be reused for a different application with zero
  changes.
- **TestNG suites** -- Three XML suite files (`main.xml`, `login.xml`, `e2e.xml`) allow running the full regression or a
  targeted slice via a single Maven property (`-Dsuite.name=...`).
- **ExtentReports** -- Generates timestamped HTML reports with step-level logs and screenshots on pass/fail, giving
  reviewers a clear picture without re-running the suite.

## Next Steps If Given More Time

1. **Broader login coverage** -- Add tests for `problem_user`, `performance_glitch_user`, `error_user`, empty fields.
2. **Product page tests** -- Sorting (A-Z, price low-high), product detail view, remove-from-cart, and cart persistence
   across navigation.
3. **Cross-browser matrix** -- Run the suite against Chrome, Firefox, and Edge in CI (GitHub Actions or similar) via a
   parameterized matrix. Run with the cross browser tools like Browserstack or LambdaTest
4. **Data-driven tests** -- Use TestNG `@DataProvider` to feed multiple credential combinations and product counts from
   an external source (CSV/JSON).
5. **Docker execution** -- Add a `docker-compose.yml` with Selenium Grid for portable, isolated runs.
6   **Test Retry** -- We can add 'RetryAnalyzer' from testNG to retry the failing tests which are failed due to flakiness.
