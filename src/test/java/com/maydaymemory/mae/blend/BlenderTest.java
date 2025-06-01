package com.maydaymemory.mae.blend;

import com.maydaymemory.mae.basic.*;
import com.maydaymemory.mae.util.MathUtil;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

public class BlenderTest {
    @Test
    public void testSimpleAdditiveBlender() {
        BoneTransformFactory transformFactory = new ZYXBoneTransformFactory();
        Vector3f translation1 = new Vector3f(1, 2, 3);
        Vector3f translation2 = new Vector3f(2, 3, 4);
        Vector3f rotation1 = new Vector3f(0, 0.5f, 0.7f);
        Vector3f rotation2 = new Vector3f(1, 0.3f, 3.14f);
        Vector3f scale1 = new Vector3f(1.27f, 0.9f, 1.1f);
        Vector3f scale2 = new Vector3f(1.05f, 1.3f, 1.2f);
        BoneTransform boneTransform1 = transformFactory.createBoneTransform(0, translation1, rotation1, scale1);
        BoneTransform boneTransform2 = transformFactory.createBoneTransform(1, translation1, rotation1, scale1);
        BoneTransform boneTransform3 = transformFactory.createBoneTransform(1, translation2, rotation2, scale2);
        BoneTransform boneTransform4 = transformFactory.createBoneTransform(2, translation2, rotation2, scale2);

        ArrayPose arrayPose = new ArrayPose(new ArrayList<>(Arrays.asList(boneTransform1, boneTransform2, boneTransform4)));
        ArrayPose arrayPose1 = new ArrayPose(new ArrayList<>(Collections.singletonList(boneTransform3)));

        AdditiveBlender blender = new SimpleAdditiveBlender(transformFactory, LinkedListPoseBuilder::new);
        Pose output = blender.blend(arrayPose1, arrayPose);
        Iterator<BoneTransform> iterator = output.getBoneTransforms().iterator();
        BoneTransform outputTransform1 = iterator.next();
        BoneTransform outputTransform2 = iterator.next();
        BoneTransform outputTransform3 = iterator.next();

        Assertions.assertEquals(translation1, outputTransform1.translation());
        Assertions.assertEquals(rotation1, outputTransform1.rotation().asEulerAngle());
        Assertions.assertEquals(scale1, outputTransform1.scale());

        Assertions.assertEquals(translation1.add(translation2, new Vector3f()), outputTransform2.translation());
        Assertions.assertEquals(rotation1.add(rotation2, new Vector3f()), outputTransform2.rotation().asEulerAngle());
        Assertions.assertEquals(scale1.mul(scale2, new Vector3f()), outputTransform2.scale());

        Assertions.assertEquals(translation2, outputTransform3.translation());
        Assertions.assertEquals(rotation2, outputTransform3.rotation().asEulerAngle());
        Assertions.assertEquals(scale2, outputTransform3.scale());
    }

    @Test
    public void testSimpleInterpolatorBlender() {
        BoneTransformFactory transformFactory = new ZYXBoneTransformFactory();
        Vector3f translation1 = new Vector3f(1, 2, 3);
        Vector3f translation2 = new Vector3f(2, 1, 3);
        Vector3f rotation1 = new Vector3f(-1.34f, 0.13f, 1.57f);
        Vector3f rotation2 = new Vector3f(1.02f, 0.33f, 3.14f);
        Vector3f scale1 = new Vector3f(1.21f, 0.9f, 1.1f);
        Vector3f scale2 = new Vector3f(1.11f, -0.3f, 0.5f);
        BoneTransform boneTransform1 = transformFactory.createBoneTransform(0, translation1, rotation1, scale1);
        BoneTransform boneTransform2 = transformFactory.createBoneTransform(1, translation2, rotation2, scale2);
        BoneTransform boneTransform3 = transformFactory.createBoneTransform(2, translation2, rotation2, scale2);
        BoneTransform boneTransform4 = transformFactory.createBoneTransform(2, translation1, rotation1, scale1);

        ArrayPose arrayPose = new ArrayPose(new ArrayList<>(Arrays.asList(boneTransform1, boneTransform2, boneTransform3)));
        ArrayPose arrayPose1 = new ArrayPose(new ArrayList<>(Collections.singletonList(boneTransform4)));

        InterpolatorBlender blender = new SimpleInterpolatorBlender(transformFactory, ArrayPoseBuilder::new);
        float weight = 0.4f;
        ArrayPose output = (ArrayPose) blender.blend(arrayPose1, arrayPose, weight);

        Assertions.assertEquals(3, output.size());
        Assertions.assertEquals(new Vector3f(0, 0, 0).lerp(translation1, 0.4f), output.get(0).translation());

        Assertions.assertEquals(new Vector3f(1,1,1).lerp(scale2, 0.4f), output.get(1).scale());

        Quaternionf quaternion1 = new Quaternionf().rotateZYX(rotation1.z, rotation1.y, rotation1.x);
        Quaternionf quaternion2 = new Quaternionf().rotateZYX(rotation2.z, rotation2.y, rotation2.x);

        Assertions.assertEquals(translation1.lerp(translation2, weight, new Vector3f()), output.get(2).translation());
        Assertions.assertEquals(MathUtil.nlerpShortestPath(quaternion1, quaternion2, weight), output.get(2).rotation().asQuaternion());
        Assertions.assertEquals(scale1.lerp(scale2, weight, new Vector3f()), output.get(2).scale());
    }

    @Test
    public void testSimpleBlendSpace1D() {
        BoneTransformFactory transformFactory = new ZYXBoneTransformFactory();

        Vector3f translationA = new Vector3f(0, 0, 0);
        Vector3f translationB = new Vector3f(5, 0, 0);
        Vector3f translationC = new Vector3f(10, 0, 0);

        Vector3f rotation = new Vector3f(0, 0, 0);
        Vector3f scale = new Vector3f(1, 1, 1);

        BoneTransform boneA = transformFactory.createBoneTransform(0, translationA, rotation, scale);
        BoneTransform boneB = transformFactory.createBoneTransform(0, translationB, rotation, scale);
        BoneTransform boneC = transformFactory.createBoneTransform(0, translationC, rotation, scale);

        ArrayPose poseA = new ArrayPose(new ArrayList<>(Collections.singletonList(boneA)));
        ArrayPose poseB = new ArrayPose(new ArrayList<>(Collections.singletonList(boneB)));
        ArrayPose poseC = new ArrayPose(new ArrayList<>(Collections.singletonList(boneC)));

        // test normal usage
        SimpleBlendSpace1D blendSpace = new SimpleBlendSpace1D(transformFactory, ArrayPoseBuilder::new);
        blendSpace.setSamplerPosition(0, 0.0f);
        blendSpace.setSamplerPose(0, poseA);

        blendSpace.setSamplerPosition(1, 0.5f);
        blendSpace.setSamplerPose(1, poseB);

        blendSpace.setSamplerPosition(2, 1.0f);
        blendSpace.setSamplerPose(2, poseC);

        float[] testPositions = {-1.0f, 0.0f, 0.25f, 0.5f, 0.75f, 1.0f, 1.5f};
        float[] expectedValueX = {0.0f, 0.0f, 2.5f, 5.0f, 7.5f, 10.0f, 10.0f};

        for (int i = 0; i < testPositions.length; i++) {
            float pos = testPositions[i];
            Pose blended = blendSpace.blend(pos);
            BoneTransform boneTransform = blended.getBoneTransforms().iterator().next();
            Assertions.assertEquals(expectedValueX[i], boneTransform.translation().x());
        }

        // test boundary usage (only one sampler point)
        SimpleBlendSpace1D blendSpace1 = new SimpleBlendSpace1D(transformFactory, ArrayPoseBuilder::new);

        blendSpace1.setSamplerPosition(0, 0.0f);
        blendSpace1.setSamplerPose(0, poseA);

        Assertions.assertEquals(blendSpace1.blend(-1.0f).getBoneTransforms().iterator().next(), poseA.get(0));
        Assertions.assertEquals(blendSpace1.blend(0.0f).getBoneTransforms().iterator().next(), poseA.get(0));
        Assertions.assertEquals(blendSpace1.blend(1.0f).getBoneTransforms().iterator().next(), poseA.get(0));
    }


    @Test
    public void testClampBlendSpace2D() {
        BoneTransformFactory transformFactory = new ZYXBoneTransformFactory();

        Vector3f translationA = new Vector3f(0, 0, 0);
        Vector3f translationB = new Vector3f(5, 0, 0);
        Vector3f translationC = new Vector3f(10, 0, 0);
        Vector3f translationD = new Vector3f(-5, 0, 0);
        Vector3f translationE = new Vector3f(-10, 0, 0);

        Vector3f rotation = new Vector3f(0, 0, 0);
        Vector3f scale = new Vector3f(1, 1, 1);

        Quaternionf rotation2 = new Quaternionf().rotationZYX((float) (Math.PI / 4.0), (float) (Math.PI / 2.0), (float)Math.PI);

        BoneTransform boneA = transformFactory.createBoneTransform(0, translationA, rotation, scale);
        BoneTransform boneB = transformFactory.createBoneTransform(0, translationB, rotation, scale);
        BoneTransform boneB2 = transformFactory.createBoneTransform(1, translationB, rotation, scale);
        BoneTransform boneC = transformFactory.createBoneTransform(0, translationC, rotation2, scale);
        BoneTransform boneC2 = transformFactory.createBoneTransform(2, translationC, rotation2, scale);
        BoneTransform boneD = transformFactory.createBoneTransform(0, translationD, rotation, scale);
        BoneTransform boneE = transformFactory.createBoneTransform(0, translationE, rotation, scale);

        ArrayPose poseA = new ArrayPose(new ArrayList<>(Collections.singletonList(boneA)));
        ArrayPose poseB = new ArrayPose(new ArrayList<>(Arrays.asList(boneB, boneB2)));
        ArrayPose poseC = new ArrayPose(new ArrayList<>(Arrays.asList(boneC, boneC2)));
        ArrayPose poseD = new ArrayPose(new ArrayList<>(Collections.singletonList(boneD)));
        ArrayPose poseE = new ArrayPose(new ArrayList<>(Collections.singletonList(boneE)));

        ClampToEdgeBlendSpace2D blendSpace = new ClampToEdgeBlendSpace2D(transformFactory, ArrayPoseBuilder::new);

        blendSpace.setSamplerPose(0, poseA);

        blendSpace.setSamplerPosition(0, 0, 0);
        blendSpace.setSamplerPosition(1, 0, 1);
        blendSpace.setSamplerPosition(2, 1, 0);
        blendSpace.setSamplerPosition(3, 0, -1);
        blendSpace.setSamplerPosition(4, -1, 0);

        blendSpace.setSamplerPose(1, poseB);

        blendSpace.triangulate();

        blendSpace.setSamplerPose(2, poseC);
        blendSpace.setSamplerPose(3, poseD);
        blendSpace.setSamplerPose(4, poseE);

        // The boundary case of input point is exactly on the sampling point
        ArrayPose output1 = (ArrayPose) blendSpace.blend(0, 1);
        BoneTransform outputTransform1 = output1.get(0);
        Assertions.assertEquals(outputTransform1.translation(), translationB);

        // The case of input point is outside every triangle and need to be clamped.
        ArrayPose output2 = (ArrayPose) blendSpace.blend(1, 1);
        Assertions.assertEquals(output2.size(), 3);
        BoneTransform outputTransform2 = output2.get(0);
        // System.out.println(outputTransform2);
        Assertions.assertEquals(outputTransform2.translation(), translationB.lerp(translationC, 0.5f, new Vector3f()));
    }

    @Test
    public void testSimpleLayeredBlender() {
        ZYXBoneTransformFactory transformFactory = new ZYXBoneTransformFactory();
        SkeletonDescendantAccessor skeleton = new SkeletonDescendantAccessor() {
            @Override
            public Collection<Integer> getDescendantBoneIndices(int rootBoneIndex, int depth) {
                if (depth == 1) {
                    return Collections.singletonList(rootBoneIndex);
                } else {
                    return rootBoneIndex == 0 ? Arrays.asList(0, 1) : Collections.singletonList(1);
                }
            }
        };

        SimpleLayeredBlender blender = new SimpleLayeredBlender(transformFactory, ArrayPoseBuilder::new);
        SkeletonBaseLayerBlend layer = new SkeletonBaseLayerBlend(skeleton);
        layer.addControlPoint(0, 2, 0.5f);

        Vector3f translationA = new Vector3f(0, 0, 0);
        Vector3f translationB = new Vector3f(5, 0, 0);
        Vector3f translationC = new Vector3f(10, 0, 0);
        Vector3f translationD = new Vector3f(15, 0, 0);

        Vector3f rotation = new Vector3f(0, 0, 0);
        Vector3f scale = new Vector3f(1, 1, 1);

        BoneTransform bone1 = transformFactory.createBoneTransform(0, translationA, rotation, scale);
        BoneTransform bone2 = transformFactory.createBoneTransform(1, translationB, rotation, scale);
        BoneTransform bone3 = transformFactory.createBoneTransform(0, translationC, rotation, scale);
        BoneTransform bone4 = transformFactory.createBoneTransform(1, translationD, rotation, scale);

        ArrayPose poseA = new ArrayPose(new ArrayList<>(Arrays.asList(bone1, bone2)));
        ArrayPose poseB = new ArrayPose(new ArrayList<>(Arrays.asList(bone3, bone4)));

        ArrayPose outputPose = (ArrayPose) blender.blend(poseA, poseB, layer);

        Assertions.assertEquals(translationA.lerp(translationC, 0.5f, new Vector3f()), outputPose.get(0).translation());
        Assertions.assertEquals(translationB.lerp(translationD, 0.5f, new Vector3f()), outputPose.get(1).translation());

        layer.addControlPoint(1, 1, 0.6f);

        outputPose = (ArrayPose) blender.blend(poseA, poseB, layer);

        Assertions.assertEquals(translationA.lerp(translationC, 0.5f, new Vector3f()), outputPose.get(0).translation());
        Assertions.assertEquals(translationB.lerp(translationD, 0.6f, new Vector3f()), outputPose.get(1).translation());
    }

    /**
     * Test efficiency of interpolation when simulating 10000 bones
     */
    @Test
    public void performanceSimpleInterpolatorBlender() {
        BoneTransformFactory transformFactory = new ZYXBoneTransformFactory();
        Vector3f translation1 = new Vector3f(1, 2, 3);
        Vector3f translation2 = new Vector3f(2, 1, 3);
        Vector3f rotation1 = new Vector3f(-1.34f, 0.13f, 1.57f);
        Vector3f rotation2 = new Vector3f(1.02f, 0.33f, 3.14f);
        Vector3f scale1 = new Vector3f(1.21f, 0.9f, 1.1f);
        Vector3f scale2 = new Vector3f(1.11f, -0.3f, 0.5f);

        PoseBuilder builder1 = new ArrayPoseBuilder(10000);
        PoseBuilder builder2 = new ArrayPoseBuilder(10000);
        for (int i = 0; i < 10000; i++) {
            builder1.addBoneTransform(transformFactory.createBoneTransform(i, translation1, rotation1, scale1));
            builder2.addBoneTransform(transformFactory.createBoneTransform(i, translation2, rotation2, scale2));
        }

        InterpolatorBlender blender = new SimpleInterpolatorBlender(transformFactory, () -> new ArrayPoseBuilder(10000));
        blender.blend(builder1.toPose(), builder2.toPose(), 0.5f);
    }
}
