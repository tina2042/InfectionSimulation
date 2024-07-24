# InfectionSimulation Program

## Overview
The InfectionSimulation program is a dynamic simulation designed to model the spread of an infection within a population. This simulation visually represents how an infection can spread and how individuals in the population transition between different health states: healthy, infected, and immune.

## Features
- **Visual Representation**: The population is represented by dots of different colors:
  - **Red Dots**: Infected individuals
  - **Green Dots**: Healthy individuals
  - **Orange Dots**: Immune individuals
- **Infection Spread**: Healthy individuals can become infected if they are near an infected individual for a certain period.
- **Health States Transition**: Individuals transition between states (healthy, infected, immune) based on the infection dynamics and implemented rules.

## Design Patterns
The program utilizes the following design patterns:

### State Pattern
The State pattern is used to manage the state transitions of individuals in the population. Each individual can be in one of the three states: Healthy, Infected, or Immune. The State pattern allows the object to change its behavior when its state changes.

### Memento Pattern
The Memento pattern is used to capture and restore the state of the simulation. This allows the simulation to save its current state at any point and revert to a previous state if needed. This is particularly useful for testing different scenarios and for providing an undo feature.


### Input Parameters
The simulation requires the following input parameters:
- **Variant 1 **: Initial population don't have immunity.
- **Variant 2 **: Initial people might have immunity.
- **Initial People **: Numer of initial people.

### Running the Simulation
1. The program will prompt for the input parameters.
2. The simulation will display the population as dots on the screen.
3. Watch the infection spread over time as healthy individuals come into contact with infected ones.

### Controls
- **Start/Stop**: You can start and stop the simulation using the provided controls.
- **Save State**: Save the current state of the simulation.
- **Load State**: Load a previously saved state to revert the simulation to that point.
