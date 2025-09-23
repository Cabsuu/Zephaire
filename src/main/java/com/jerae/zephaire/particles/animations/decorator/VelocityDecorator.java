package com.jerae.zephaire.particles.animations.decorator;

import com.jerae.zephaire.particles.animations.AnimatedParticle;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.bukkit.ChatColor;

import java.util.concurrent.ThreadLocalRandom;

public class VelocityDecorator implements AnimatedParticle {

    private final AnimatedParticle wrappedParticle;
    private final Vector velocity;
    private final String type; // gravity, bounce, fountain, spray
    private final double bounceFactor;
    private final Location groundLocation;
    private final Location initialLocation;
    private final Vector initialVelocity;
    private final double spread;

    public VelocityDecorator(AnimatedParticle wrappedParticle, Vector velocity, String type, double bounceFactor, Location groundLocation, double spread) {
        this.wrappedParticle = wrappedParticle;
        this.velocity = velocity.clone();
        this.type = type;
        this.bounceFactor = bounceFactor;
        this.groundLocation = groundLocation;
        this.initialLocation = wrappedParticle.getCurrentLocation().clone();
        this.initialVelocity = velocity.clone();
        this.spread = spread;
    }

    @Override
    public void tick() {
        wrappedParticle.tick();

        Location loc = wrappedParticle.getCurrentLocation();
        if (loc == null) return;

        switch (type.toLowerCase()) {
            case "gravity":
                loc.add(velocity);
                velocity.setY(velocity.getY() - 0.05); // simple gravity
                break;
            case "bounce":
                loc.add(velocity);
                if (groundLocation != null && loc.getY() <= groundLocation.getY()) {
                    loc.setY(groundLocation.getY());
                    velocity.setY(velocity.getY() * -bounceFactor);
                }
                velocity.setY(velocity.getY() - 0.05); // gravity for bounce
                break;
            case "fountain":
                loc.add(velocity);
                velocity.setY(velocity.getY() - 0.05);
                if (loc.getY() < initialLocation.getY()) {
                    loc.setX(initialLocation.getX());
                    loc.setY(initialLocation.getY());
                    loc.setZ(initialLocation.getZ());
                    velocity.setX(initialVelocity.getX());
                    velocity.setY(initialVelocity.getY());
                    velocity.setZ(initialVelocity.getZ());
                }
                break;
            case "spray":
                Vector random = new Vector(
                        ThreadLocalRandom.current().nextDouble(-spread, spread),
                        ThreadLocalRandom.current().nextDouble(-spread, spread),
                        ThreadLocalRandom.current().nextDouble(-spread, spread)
                );
                loc.add(velocity.clone().add(random));
                break;
        }
    }

    @Override
    public Location getCurrentLocation() {
        return wrappedParticle.getCurrentLocation();
    }

    @Override
    public boolean shouldCollide() {
        return wrappedParticle.shouldCollide();
    }

    @Override
    public int getLoopDelayCounter() {
        return wrappedParticle.getLoopDelayCounter();
    }

    @Override
    public String getDebugInfo() {
        StringBuilder info = new StringBuilder();
        info.append(ChatColor.YELLOW).append("--- Decorator: Velocity ---").append("\n");
        info.append(ChatColor.AQUA).append("Type: ").append(ChatColor.WHITE).append(type).append("\n");
        info.append(ChatColor.AQUA).append("Velocity: ").append(ChatColor.WHITE).append(String.format("%.2f, %.2f, %.2f", velocity.getX(), velocity.getY(), velocity.getZ())).append("\n");
        info.append(ChatColor.DARK_AQUA).append("--- Wrapped Particle ---").append("\n");
        info.append(wrappedParticle.getDebugInfo());
        return info.toString();
    }

    public Vector getVelocity() {
        return velocity;
    }
}
