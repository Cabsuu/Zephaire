package com.jerae.zephaire.particles.util;

import com.jerae.zephaire.particles.ParticleScheduler;
import com.jerae.zephaire.particles.ParticleSpawnData;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ParticleDrawingUtils {

    public static void drawStar(Location center, int points, double outerRadius, double innerRadius, double rotationAngle, Vector rotation, Vector[] vertices) {
        int totalVertices = points * 2;
        Vector reusableVertex = new Vector();

        for (int i = 0; i < totalVertices; i++) {
            double angle = rotationAngle + (i * Math.PI / points);
            double radius = (i % 2 == 0) ? outerRadius : innerRadius;
            reusableVertex.setX(Math.cos(angle) * radius).setY(0).setZ(Math.sin(angle) * radius);
            VectorUtils.rotateVector(reusableVertex, rotation, vertices[i]);
        }
    }

    public static void drawParticleLine(Location center, Vector start, Vector end, double density, Particle particle, Object options, int despawnTimer, boolean hasGravity) {
        Vector lineDirection = new Vector();
        Vector currentLinePoint = new Vector();
        Location particleLoc = center.clone();

        lineDirection.copy(end).subtract(start);
        double length = lineDirection.length();
        lineDirection.normalize();

        for (double d = 0; d < length; d += (1.0 / density)) {
            currentLinePoint.copy(lineDirection).multiply(d).add(start);
            particleLoc.setX(center.getX() + currentLinePoint.getX());
            particleLoc.setY(center.getY() + currentLinePoint.getY());
            particleLoc.setZ(center.getZ() + currentLinePoint.getZ());

            if (particle == null && options instanceof ItemStack) {
                ParticleScheduler.queueParticle(new ParticleSpawnData(particleLoc, (ItemStack) options, despawnTimer, hasGravity));
            } else if (particle != null) {
                if (particle == Particle.SHRIEK && options instanceof Integer) {
                    ParticleScheduler.queueParticle(new ParticleSpawnData(particle, particleLoc, (Integer) options));
                } else if (particle == Particle.VIBRATION && options instanceof org.bukkit.Vibration) {
                    ParticleScheduler.queueParticle(new ParticleSpawnData(particle, particleLoc, (org.bukkit.Vibration) options));
                } else if (particle == Particle.SCULK_CHARGE && options instanceof Float) {
                    ParticleScheduler.queueParticle(new ParticleSpawnData(particle, particleLoc, (Float) options));
                } else if (particle == Particle.TRAIL && options instanceof Integer) {
                    ParticleScheduler.queueParticle(new ParticleSpawnData(particle, particleLoc, (Integer) options, hasGravity));
                } else {
                    ParticleScheduler.queueParticle(new ParticleSpawnData(particle, particleLoc, 1, 0, 0, 0, 0, options));
                }
            }
        }
    }
}
