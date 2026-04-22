# SauceDemo Test Automation Framework

Selenium + Java test automation for [saucedemo.com](https://www.saucedemo.com), built with TestNG, Page Object Model, and ExtentReports.

---

## Prerequisites

| Tool  | Version | Notes |
|-------|---------|-------|
| Java  | 14+     | `java -version` to verify |
| Maven | 3.6+    | `mvn -version` to verify |
| Chrome, Firefox, or Edge | Latest stable | ChromeDriver is managed automatically by WebDriverManager |

> No manual driver downloads required -- WebDriverManager handles browser driver binaries at runtime.

---

## Project Structure

```
SauceDemo/
├── pom.xml                          # Maven config & dependencies
├── src/test/
│   ├── java/com/demo/
│   │   ├── pages/                   # Page Objects (LoginPage, DashboardPage, CartPage, CheckoutPage)
│   │   └── tests/                   # Test classes (LoginTests, E2ETests)
│   ├── java/com/framework/
│   │   ├── base/BaseDriver.java     # Setup / teardown, ThreadLocal driver
│   │   ├── config/ConfigReader.java # Loads config.properties
│   │   ├── driver/                  # DriverInit (factory) + DriverDesign (thread-safe holder)
│   │   ├── listeners/               # ExtentReports listener, retry analyzer
│   │   └── utils/DataGenerator.java # Random test data (names, ZIP codes)
│   └── resources/config.properties  # Browser, timeouts, credentials, report settings
└── test-suites/
    ├── main.xml                     # Full regression (Login + E2E, 3 threads)
    ├── login.xml                    # Login tests only
    └── e2e.xml                      # E2E checkout tests only
```

---

## Quick Start

```bash
# Clone and enter the project
git clone <repo-url> && cd SauceDemo

# Run the full suite (default: main.xml)
mvn clean test

# Run a specific suite
mvn clean test -Dsuite.name=login.xml
mvn clean test -Dsuite.name=e2e.xml
```

---

## Configuration

All runtime settings live in `src/test/resources/config.properties`:

| Property             | Default          | Description                        |
|----------------------|------------------|------------------------------------|
| `browser`            | `chrome`         | `chrome`, `firefox`, or `edge`     |
| `headless`           | `true`           | Run without a visible browser      |
| `implicit.wait`      | `10`             | Implicit wait (seconds)            |
| `explicit.wait`      | `15`             | Explicit wait (seconds)            |
| `page.load.timeout`  | `30`             | Page load timeout (seconds)        |
| `retry.count`        | `2`              | Auto-retries for failed tests      |

To run headed (see the browser):

```properties
headless=false
```

---

## Test Coverage

### Login Tests (`LoginTests.java`)
| Test | Validates |
|------|-----------|
| `loginWithValidCredentials` | Standard user can log in and reach the dashboard |
| `loginWithLockedOutUser` | Locked-out user sees the correct error message |

### E2E Tests (`E2ETests.java`)
| Test | Validates |
|------|-----------|
| `completeCheckoutWithTwoProducts` | Full purchase flow: login, add 2 items, verify cart totals, checkout with random data, confirm order success, and validate price integrity at every step |

---

## Reports

After a run, HTML reports are generated in:

```
test-output/ExtentReports/Automation_Test_Report_<timestamp>.html
```

Open the HTML file in a browser to view pass/fail status, step logs, and screenshots.

---

## Sample Run Output

```
[INFO] Running TestSuite
[01:05:17] [TEST START] completeCheckoutWithTwoProducts
[01:05:17] [TEST START] loginWithLockedOutUser
[01:05:17] [INFO] Login with user: standard_user
[01:05:17] [INFO] Login with user: locked_out_user
[01:05:17] [INFO] Error message: Epic sadface: Sorry, this user has been locked out.
[01:05:18] [PASS] loginWithLockedOutUser (0s)
[01:05:18] [INFO] Added to cart: Sauce Labs Backpack @ $29.99
[01:05:18] [INFO] Added to cart: Sauce Labs Bike Light @ $9.99
[01:05:18] [INFO] Cart subtotal (calculated): $39.98
[01:05:18] [INFO] Order overview subtotal: $39.98
[01:05:19] [INFO] Success header: Thank you for your order!
[01:05:19] [PASS] completeCheckoutWithTwoProducts (1s)
[01:05:19] [TEST START] loginWithValidCredentials
[01:05:20] [PASS] loginWithValidCredentials (0s)
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## Parallel Execution

Tests run in parallel via TestNG's `parallel="methods"` (default: 3 threads). Thread safety is guaranteed by `ThreadLocal<WebDriver>` -- each test method gets its own browser instance.
