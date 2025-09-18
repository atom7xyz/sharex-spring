![Java](https://img.shields.io/badge/Java-21-216B00?style=for-the-badge&color=3d85c6)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-216B00?style=for-the-badge&color=2D923C)
![License](https://img.shields.io/github/license/atom7xyz/sharex-spring?style=for-the-badge&color=7469B6)

# sharex-spring

A self-hosted server for ShareX, supporting image uploads, text sharing, file hosting, and URL shortening.

---

## Links
- [Features](https://github.com/atom7xyz/sharex-spring#Features)
- [Installation](https://github.com/atom7xyz/sharex-spring/wiki/Installation)
- [Configuration](https://github.com/atom7xyz/sharex-spring/wiki/Configuration)

---

## Features

| Feature                          | Implemented | Description                                                     |
|----------------------------------|-------------|-----------------------------------------------------------------|
| File/Image/Text Uploader Service | Yes         | Multi-format support (e.g., PNG, JPG, TXT and more).            |
| URL Shortener Service            | Yes         | Binds a long URL to a unique, shortened alias for easy sharing. |
| Built in HTTPS Support           | Yes         | Secure communication via HTTPS (keystore/certificates).         |
| API Key Authenticator            | Yes         | REST API authentication via per-user API KEYs.                  |
| Request Rate Limiter             | Yes         | Sliding Window Log algorithm for throttling.                    |
| Customizable File Limits         | Yes         | User-defined file upload restrictions (type, size).             |
| Caching (In-Memory)              | Yes         | Caching for frequent requests.                                  |
| Docker Support                   | Yes         | Pre-configured Docker compose files.                            |
| Logging                          | Yes         | Real-time request logs.                                         |
| Duplicate checker                | Yes         | Checks and handles duplicates in the uploads.                   |
| Web interface                    | No          | *Planned:* Web interface to interact with uploaded images       |
| Account creation                 | No          | *Planned:* Account creation for other users.                    |

---

## License
This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.
