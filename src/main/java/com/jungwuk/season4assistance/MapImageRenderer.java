package com.jungwuk.season4assistance;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;

import java.awt.image.BufferedImage;

public class MapImageRenderer extends MapRenderer {
    public MapImageRenderer(boolean v) {
        super(v);
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        BufferedImage bufferedImage = new BufferedImage(255, 255, BufferedImage.TYPE_INT_ARGB);
        bufferedImage.createGraphics().fillRect(0, 0, 255, 255);
        canvas.drawImage(0, 0, bufferedImage);
        canvas.drawText(15, 15, MinecraftFont.Font, "Your name : " + player.getName());
    }
}
