# 14 - Design System

Version: 1.0

Status: Active

---

# Purpose

The OniCanvas Design System defines every reusable UI component, visual token, spacing rule, typography guideline, and interaction pattern used throughout the application.

Goals

• Consistency

• Reusability

• Accessibility

• Scalability

• Professional appearance

Every screen should be built from these components instead of creating custom UI repeatedly.

---

# Design Philosophy

The interface should feel

Professional

Creative

Minimal

Fast

Touch-first

Comfortable during long drawing sessions.

---

# Foundations

## Material Design

Material 3

Compose-first

Dynamic Color

Optional

Glassmorphism

Soft shadows

Rounded surfaces

Subtle blur

---

# Grid System

8dp spacing system

Spacing

4dp

8dp

12dp

16dp

24dp

32dp

48dp

64dp

---

# Corner Radius

Small

8dp

Medium

16dp

Large

24dp

Floating Panel

28dp

Dialog

32dp

---

# Elevation

Level 0

Flat

Level 1

Cards

Level 2

Floating Panels

Level 3

Dialogs

Use blur whenever possible instead of heavy shadows.

---

# Color Tokens

Primary

Secondary

Tertiary

Surface

Surface Variant

Background

Primary Container

Error

Warning

Success

Info

Never hardcode colors.

Always reference Material Theme.

---

# Typography

Display

Headline

Title

Body

Label

Caption

Maximum three text sizes per screen.

---

# Icons

Material Symbols Rounded

24dp default

20dp compact

32dp emphasis

48dp hero

---

# Component Library

## Primary Button

Purpose

Main action.

States

Normal

Pressed

Disabled

Loading

---

## Secondary Button

Purpose

Supporting actions.

Outlined style.

---

## Icon Button

Purpose

Toolbar actions.

Supports

Filled

Outlined

Tonal

---

## Floating Action Button

Purpose

Single important action.

Maximum one per screen.

---

## Glass Card

Purpose

Reusable content container.

Supports

Header

Body

Footer

Optional icon

Optional status

---

## Status Card

Displays

Connection

Battery

Latency

Software

Workspace

Supports

Success

Warning

Error

Info

---

## Section Header

Contains

Title

Subtitle

Optional Action

---

## Macro Button

Most important component.

Contains

Icon

Label

Color

Optional Badge

Optional Long Press Indicator

Supports

Small

Medium

Large

Animated press feedback

---

## Radial Menu Item

Contains

Icon

Label

Selection Highlight

Supports

4

6

8

12

16

items

---

## Slider

Used for

Zoom

Opacity

Brush Size

Sensitivity

Supports

Continuous

Discrete

---

## Toggle

Used for

Settings

Developer Options

Feature Flags

---

## Text Field

Supports

Single line

Multi-line

Validation

Helper Text

Leading icon

Trailing icon

---

## Dialog

Supports

Confirmation

Input

Warning

Information

Fullscreen

---

## Bottom Sheet

Used for

Workspace Selection

Brush List

Palette Selection

Settings

---

## Snackbar

Short messages only.

Duration

Short

Long

Action

Optional

---

## Loading Indicator

Circular

Linear

Skeleton Loading

---

## Empty State

Contains

Illustration

Title

Description

Primary Action

---

## Error State

Contains

Icon

Message

Retry Button

---

# Navigation Components

Top App Bar

Bottom Navigation

Navigation Rail

Tablet Drawer

---

# Animations

Duration

150–250ms

Types

Fade

Scale

Slide

Crossfade

Avoid

Bounce

Flash

Overshoot

---

# Motion

Every interaction should provide feedback.

Examples

Button Press

Card Selection

Workspace Switch

Connection Success

---

# Haptics

Enabled for

Primary Buttons

Macro Buttons

Radial Menu

Gesture Completion

Connection Success

---

# Tablet Rules

Two-pane layouts

Resizable panels

Landscape optimization

Larger macro grids

Persistent navigation

---

# Accessibility

48dp touch targets

Dynamic text

High contrast

Reduced motion

Screen reader support

---

# Component Naming

PrimaryButton

SecondaryButton

MacroButton

GlassCard

StatusCard

SectionHeader

ConnectionIndicator

WorkspaceCard

ProfileCard

LoadingView

EmptyState

ErrorView

---

# Design Principles

- Reuse before creating
- Keep components stateless
- Support theming
- Minimize customization through code
- Prefer composition over inheritance

---

# Summary

The OniCanvas Design System is the single source of truth for the application's visual language.

All new screens and features should be composed from these reusable components to ensure consistency, maintainability, and a premium user experience.