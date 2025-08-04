# 🎬 Mayday Animation Engine

[![Java](https://img.shields.io/badge/Java-8+-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)
[![Version](https://img.shields.io/badge/Version-1.0.2-blue.svg)](https://github.com/286799714/MaydayAnimationEngine/releases)

> A powerful Java runtime animation library for creating smooth, high-performance animations and pose blending systems.

## ✨ Features

- 🎯 **High Performance**: Optimized for real-time animation with efficient memory management
- 🔄 **Advanced Blending**: Support for blendspace, layered, kinematic interpolation blending
- 🎪 **Keyframe Animation**: Flexible keyframe system with multiple interpolation methods
- 🏗️ **Modular Design**: Clean architecture with extensible components
- ⚡ **Real-time Ready**: Designed for game engines and real-time applications

## 🚀 Quick Start

### Prerequisites

- Java 8 or higher
- Gradle (for building from source)

### Installation

#### Maven Dependency

```xml
<dependency>
    <groupId>com.maydaymemory</groupId>
    <artifactId>mae</artifactId>
    <version>1.0.3</version>
</dependency>
```

#### Gradle Dependency

```gradle
implementation 'com.maydaymemory:mae:1.0.3'
```

#### Manual Installation

```bash
git clone https://github.com/286799714/MaydayAnimationEngine.git
cd MaydayAnimationEngine
./gradlew publishToMavenLocal
```

## 📖 Usage Examples

### Basic Animation Setup

```java
// Create a bone transform factory
BoneTransformFactory transformFactory = new ZYXBoneTransformFactory();

// Create a pose builder supplier
Supplier<PoseBuilder> poseBuilderSupplier = LinkedListPoseBuilder::new;

// Create an animation
Animation animation = new BasicAnimation("walk", factory, poseBuilderSupplier);

// Set up translation channel for bone 0
InterpolatableChannel<Vector3fc> translationChannel = new ArrayInterpolatableChannel<>();
translationChannel.addKeyframe(new Vector3fKeyframe(0.0f, new Vector3f(0, 0, 0)));
translationChannel.addKeyframe(new Vector3fKeyframe(1.0f, new Vector3f(1, 0, 0)));

animation.setTranslationChannel(0, translationChannel);

// Evaluate animation at time 0.5s
Pose pose = animation.evaluate(0.5f);
```

### Pose Blending

```java
// Create a blender
AdditiveBlender blender = new SimpleAdditiveBlender(transformFactory, poseBuilderSupplier);

// Blend two poses
Pose basePose = // ... your base pose
Pose additivePose = // ... your additive pose
Pose result = blender.blend(basePose, additivePose);
```

### Blend Space Usage

```java
// Create a 1D blend space
BlendSpace1D blendSpace = new SimpleBlendSpace1D(factory, poseBuilderSupplier);

// Add sample poses
blendSpace.addSampler(0.0f, idlePose);
blendSpace.addSampler(1.0f, walkPose);
blendSpace.addSampler(2.0f, runPose);

// Blend at speed 1.5
Pose blendedPose = blendSpace.blend(1.5f);
```

## 🏗️ Architecture

### Core Components

- **Animation**: Main interface for animation playback and evaluation
- **Pose**: Represents a skeletal pose with bone transformations
- **Blender**: Handles pose blending operations (additive, additional, kinematic, blendspace)
- **Controlling**: Define animation graphs, nodes, parameter passing, and animation state machines

### Package Structure

```
src/main/java/com/maydaymemory/mae/
├── basic/          # Animation and pose definition
├── blend/          # Blending algorithms and blend spaces
├── control/        # Control flow and node system
└── util/           # Utility classes and math functions
```

## 📚 Documentation

To be done...

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

### Development Setup

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

### Code Style

- Follow Java coding conventions
- Add comprehensive Javadoc comments
- Include unit tests for new features
- Ensure all tests pass before submitting

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [JOML](https://github.com/JOML-CI/JOML) - Java OpenGL Math Library
- [FastUtil](https://github.com/vigna/fastutil) - Fast and compact type-specific collections
- [Robert Penner](http://www.robertpenner.com/easing/) - Easing functions

## 📞 Support

- 📧 Email: maydaymemory123@gmail.com
- 🐛 [Report Issues](https://github.com/286799714/MaydayAnimationEngine/issues)

## 📊 Project Status

![GitHub stars](https://img.shields.io/github/stars/286799714/MaydayAnimationEngine?style=social)
![GitHub forks](https://img.shields.io/github/forks/286799714/MaydayAnimationEngine?style=social)
![GitHub issues](https://img.shields.io/github/issues/286799714/MaydayAnimationEngine)
![GitHub pull requests](https://img.shields.io/github/issues-pr/286799714/MaydayAnimationEngine)

---

<div align="center">
  <p>Made with ❤️ by <a href="https://github.com/286799714">MaydayMemory</a></p>
  <p>If this project helps you, please give it a ⭐️</p>
</div> 