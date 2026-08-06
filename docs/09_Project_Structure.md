# 09 - Project Structure

Version: 1.0
Status: Planning

---

# Purpose

This document defines the recommended project structure for OniCanvas.

The architecture is designed to be:

- Modular
- Scalable
- Easy to maintain
- Easy to test
- AI-friendly
- Feature-oriented

Every new feature should fit into this structure without requiring major refactoring.

---

# Architecture

Pattern

MVVM

Using

Jetpack Compose

Material 3

Navigation Compose

StateFlow

Coroutines

Repository Pattern

DataStore

Room

---

# Project Layout

app/

core/

feature/

navigation/

service/

data/

domain/

---

# Root Structure

app/

├── core/

├── data/

├── domain/

├── feature/

├── navigation/

├── service/

├── MainActivity

---

# Core Module

Contains shared code.

core/

designsystem/

ui/

model/

theme/

utils/

extensions/

constants/

icons/

---

## core/designsystem

Contains reusable UI components.

Examples

PrimaryButton

GlassCard

SectionHeader

StatusCard

MacroButton

RoundIconButton

ColorWheel

Slider

Dialog

LoadingView

---

## core/theme

Contains

Typography

Color Scheme

Shapes

Spacing

Dimensions

Animations

Dark Theme

Light Theme

---

## core/model

Shared models.

Examples

Workspace

Macro

Button

Action

Palette

Connection

Profile

---

## core/utils

Helpers

Logger

Date

JSON

Network

Validation

Permission Helpers

---

## core/constants

Application constants.

Ports

Timeouts

Animation Durations

Default Sizes

Version Numbers

---

# Data Layer

Responsible for storage.

data/

database/

repository/

datastore/

network/

mapper/

---

## database

Room

Entities

DAO

Database

Migrations

---

## repository

Repositories

WorkspaceRepository

SettingsRepository

ConnectionRepository

ProfileRepository

---

## datastore

Application settings.

Theme

Preferences

Last Workspace

---

## network

Communication

WebSocket

USB

Bluetooth

Protocol

---

## mapper

Converts

Database

↓

Domain

↓

UI

---

# Domain Layer

Business logic.

domain/

model/

repository/

usecase/

---

## Use Cases

ConnectDevice

DisconnectDevice

LoadWorkspace

SaveWorkspace

ExecuteCommand

ImportProfile

ExportProfile

UpdateTheme

---

# Feature Layer

Every feature owns its code.

feature/

dashboard/

workspace/

controls/

connection/

settings/

about/

---

# Dashboard

dashboard/

ui/

viewmodel/

state/

components/

---

# Workspace

workspace/

ui/

viewmodel/

repository/

editor/

components/

---

# Controls

Contains

Macro Pad

Gesture Pad

Touchpad

Radial Menu

Zoom

Layers

Brushes

Color

Canvas

Each control should be its own package.

---

Example

controls/

macro/

gesture/

touchpad/

radial/

zoom/

layers/

brush/

color/

canvas/

---

# Navigation

navigation/

Routes

NavGraph

Bottom Navigation

Deep Links

---

# Services

Background services.

service/

ConnectionService

DiscoveryService

ForegroundService

---

# MainActivity

Responsibilities

Set Theme

Host Navigation

Initialize App

Nothing else.

Business logic belongs in ViewModels.

---

# Naming Convention

Composable

DashboardScreen

ViewModel

DashboardViewModel

State

DashboardUiState

Repository

WorkspaceRepository

Use Case

ConnectDeviceUseCase

---

# UI State

Every screen should expose

Loading

Success

Error

Empty

Avoid multiple Boolean flags.

---

# State Management

ViewModel

↓

StateFlow

↓

Compose UI

The UI should never modify state directly.

---

# Dependency Rules

Feature

↓

Domain

↓

Data

↓

Core

Never allow Core to depend on Feature.

Avoid circular dependencies.

---

# Resources

res/

drawable/

font/

mipmap/

values/

xml/

---

# Assets

assets/

icons/

profiles/

templates/

sample-data/

---

# Testing

test/

unit/

integration/

ui/

---

# Logging

Debug builds

Enable verbose logging.

Release builds

Disable debug logging.

---

# Future Modules

plugin/

cloud/

voice/

analytics/

marketplace/

---

# Design Principles

- One responsibility per package
- Keep features isolated
- Reuse UI components
- Avoid duplicate code
- Prefer composition over inheritance
- Keep Composables stateless
- Business logic belongs in ViewModels
- Use immutable UI state

---

# Summary

The OniCanvas project structure is organized around features rather than layers alone.

This approach keeps related files together, improves maintainability, and scales well as new features are added throughout development.