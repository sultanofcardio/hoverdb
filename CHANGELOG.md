# Changelog - hoverdb
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.1.0] - 2020-01-10
### Added
- Created Literal value for things like SYSDATE

## [2.0.0] - 2019-12-09
### Added
- New Database.query method that takes a handler for ResultSet. Resources are automatically closed once handler finishes
- Kotlin extension methods for ResultSet

### Changed
- Database.query now returns ResourceSet - a holder class for both ResultSet and Statement
- Made ResourceSet implement AutoCloseable
- Stopped trapping SQLException is model classes. Callers will now have to handle SQLException in their code

## [1.0.1] - 2019-11-28
### Added
- Null safety to statement/query formatting
- Kotlin language support

### Removed
- Unnecessary dependencies

## [1.0.0] - 2019-09-11
First release
