# 🤝 Contributing to Mayday Animation Engine

Thank you for your interest in contributing to Mayday Animation Engine! This document provides guidelines and information for contributors.

## 📋 Table of Contents

- [Development Setup](#development-setup)
- [Coding Standards](#coding-standards)
- [Testing](#testing)
- [Pull Request Process](#pull-request-process)
- [Reporting Bugs](#reporting-bugs)
- [Feature Requests](#feature-requests)

## 🛠️ Development Setup

### Prerequisites

- Java 8 or higher
- Gradle 7.0 or higher
- Git

### Setup

1. **Fork the repository**

2. **Add upstream remote**
   ```bash
   git remote add upstream https://github.com/286799714/MaydayAnimationEngine.git
   ```

3. **Build the project**
   ```bash
   ./gradlew build
   ```

4. **Run tests**
   ```bash
   ./gradlew test
   ```

5. **Generate documentation**
   ```bash
   ./gradlew javadoc
   ```

## 📝 Coding Standards

### Java Code Style

- Follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- Use meaningful variable and method names
- Keep methods focused and concise

### Javadoc Requirements

- All public classes, methods, and fields have Javadoc as much as possible.
- Include `@param`, `@return`, and `@throws` tags where applicable
- Use proper HTML formatting in descriptions
- Include code examples for complex methods

### Code Organization

- Keep related classes in the same package
- Use appropriate access modifiers
- Minimize coupling between components
- Follow SOLID principles

### Example Javadoc

```java
/**
 * Performs additive blending of two poses.
 * 
 * <p>This method combines the transformations of two poses using additive blending:
 * <ul>
 *   <li>Translations are added component-wise</li>
 *   <li>Rotations are combined using Euler angle addition</li>
 *   <li>Scales are multiplied component-wise</li>
 * </ul>
 * </p>
 * 
 * @param basePose the base pose to blend from
 * @param additivePose the pose to add to the base pose
 * @return the blended pose result
 * @throws IllegalArgumentException if either pose is null
 */
public Pose blend(Pose basePose, Pose additivePose) {
    // Implementation
}
```

## 🧪 Testing

### Test Requirements

- All new features must include unit tests
- Use descriptive test method names

### Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests com.maydaymemory.mae.basic.AnimationTest
```

### Test Structure

```java
@Test
void shouldBlendPosesWithAdditiveBlending() {
    // Given
    Pose basePose = createBasePose();
    Pose additivePose = createAdditivePose();
    
    // When
    Pose result = blender.blend(basePose, additivePose);
    
    // Then
    assertThat(result).isNotNull();
    assertThat(result.getBoneTransform(0).translation())
        .isEqualTo(expectedTranslation);
}
```

## 🔄 Pull Request Process

### Before Submitting

1. **Update your fork**
   ```bash
   git fetch upstream
   git checkout main
   git merge upstream/main
   ```

2. **Create a feature branch**
   
   ```bash
   git checkout -b feature/your-feature-name
   ```
   
3. **Make your changes**
   - Write code following coding standards
   - Add comprehensive tests
   - Update documentation

4. **Test your changes**
   ```bash
   ./gradlew clean build
   ./gradlew test
   ```

5. [Create new pull request]([Pull requests · 286799714/MaydayAnimationEngine](https://github.com/286799714/MaydayAnimationEngine/pulls))

## 🐛 Reporting Bugs

### Bug Report Template

```markdown
## Bug Description
Brief description of the bug

## Steps to Reproduce
1. Step 1
2. Step 2
3. Step 3

## Expected Behavior
What should happen

## Actual Behavior
What actually happens

## Environment
- OS: [e.g., Windows 10, macOS 12.0]
- Java Version: [e.g., OpenJDK 11.0.12]
- MAE Version: [e.g., 1.0.1]

## Additional Information
- Error messages
- Stack traces
- Screenshots
- Related issues
```

## 💡 Feature Requests

### Feature Request Template

```markdown
## Feature Description
Brief description of the requested feature

## Use Cases
- Use case 1
- Use case 2
- Use case 3

## Proposed Solution
Description of how the feature could be implemented

## Alternatives Considered
Other approaches that were considered

## Additional Information
- Related features
- Implementation complexity
- Performance considerations
```

## 🙏 Recognition

Contributors will be recognized in:
- Project README
- Release notes

Thank you for contributing to Mayday Animation Engine! 🎉 