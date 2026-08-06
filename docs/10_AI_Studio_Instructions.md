# 10 - AI Studio Instructions

Version: 1.0
Status: Active

---

# Purpose

This document defines the development rules that Google AI Studio should follow when generating or modifying OniCanvas.

These instructions take priority over assumptions made by the AI.

The AI should always preserve the existing architecture and coding style.

---

# General Rules

Always inspect the existing project before generating code.

Do not replace working code unnecessarily.

Extend existing components whenever possible.

Never rewrite the project structure unless requested.

Generate production-quality code.

Avoid placeholder implementations unless explicitly requested.

---

# Development Philosophy

Build OniCanvas incrementally.

Every generated feature must:

- Compile successfully
- Follow project architecture
- Include documentation where appropriate
- Be reusable
- Be testable

Do not generate multiple unrelated features in a single response.

---

# Architecture

Use

MVVM

Jetpack Compose

Material 3

Navigation Compose

Repository Pattern

StateFlow

Coroutines

Room

DataStore

Kotlin Serialization

---

# UI Rules

Composables must remain stateless whenever possible.

Business logic belongs inside ViewModels.

Never call repositories directly from Composables.

Hoist UI state.

Prefer reusable components.

Avoid duplicate UI code.

---

# State Management

Every screen exposes

UiState

Loading

Success

Error

Empty

Avoid multiple Boolean state variables.

---

# Navigation

Use Navigation Compose.

Single Activity architecture.

Navigation events should originate from the ViewModel.

Preserve screen state whenever possible.

---

# ViewModels

Each feature owns one ViewModel.

Responsibilities

Load data

Manage state

Handle actions

Call repositories

Expose immutable StateFlow

---

# Repository Rules

Repositories are responsible for

Storage

Networking

Caching

Repositories should not contain UI logic.

---

# Data Models

Reuse existing models.

Do not duplicate data classes.

When adding new properties, maintain backward compatibility.

---

# Communication

Use the protocol defined in

08_Communication_Protocol.md

Never invent new message formats unless the protocol document is updated.

---

# UI Components

Before creating a new component

Search the Design System.

Reuse existing components whenever possible.

Examples

PrimaryButton

GlassCard

MacroButton

StatusCard

Dialog

Slider

---

# Themes

Support

Dark

Light

Respect Material 3 color tokens.

Do not hardcode colors.

---

# Performance

Avoid unnecessary recomposition.

Use remember only for UI state.

Use rememberSaveable where needed.

Use derivedStateOf when appropriate.

Use LazyColumn instead of scrollable Column for long lists.

---

# Error Handling

Handle failures gracefully.

Display user-friendly messages.

Never silently ignore exceptions.

---

# Logging

Debug

Verbose logging

Release

Minimal logging

Sensitive information must never be logged.

---

# Accessibility

Support

Screen readers

Dynamic font size

Minimum 48dp touch targets

High contrast

Reduced motion

---

# Code Style

Prefer

Small functions

Descriptive names

Immutable data

Single Responsibility Principle

Avoid

God classes

Long Composables

Deep nesting

Duplicate logic

---

# File Organization

Place new files in the correct feature package.

Avoid creating miscellaneous folders.

Follow the project structure defined in

09_Project_Structure.md

---

# Documentation

Public classes should include documentation.

Complex logic should include comments explaining why, not what.

Update documentation when behavior changes.

---

# Dependencies

Before introducing a new library

Verify whether existing libraries already provide the required functionality.

Avoid unnecessary dependencies.

---

# Feature Development Process

When implementing a new feature

1.

Review Feature Specification

↓

2.

Review Data Models

↓

3.

Review Communication Protocol

↓

4.

Review UI Guidelines

↓

5.

Implement

↓

6.

Test

↓

7.

Document

---

# Things To Avoid

Do not rewrite existing architecture.

Do not introduce inconsistent UI.

Do not duplicate models.

Do not hardcode strings.

Do not hardcode dimensions.

Do not ignore error states.

Do not mix UI and business logic.

---

# Preferred Output

When generating code

Explain

- What changed
- Why it changed

Then provide the implementation.

When multiple files are modified

Clearly separate each file.

---

# Goal

Google AI Studio should behave like a senior Android engineer working on a long-term production project.

Every response should preserve consistency, maintainability, and code quality while moving OniCanvas closer to release.