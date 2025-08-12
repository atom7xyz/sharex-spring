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

| Feature                          | Implemented | Description                                                               |
|----------------------------------|-------------|---------------------------------------------------------------------------|
| File/Image/Text Uploader Service | Yes         | Multi-format support (e.g., PNG, JPG, TXT).                               |
| URL Shortener Service            | Yes         | Binds a long URL to a unique, shortened alias for easy sharing.           |
| API Key Authenticator            | Yes, basic  | REST API authentication via a single API KEY.                             |
| Request Rate Limiter             | Yes         | Sliding Window Log algorithm for throttling (default: 50 req/min per IP). |
| Customizable File Limits         | Yes, basic  | User-defined file size restrictions (default: 50MB/file).                 |
| Caching (In-Memory)              | Yes, basic  | Caching for frequent requests (default TTL: 1h).                          |
| Docker Support                   | Yes         | Pre-configured Docker compose files.                                      |
| Duplicate checker                | No          | *Planned:* Checks and handles duplicates in the uploads.                  |
| Web interface                    | No          | *Planned:* Web interface to interact with uploaded images                 |
| Account creation                 | No          | *Planned:* Account creation for other users.                              |
| Logging & Analytics              | No          | *Planned*: Real-time request logs and dashboard for usage metrics.        |

---

## License
This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.
