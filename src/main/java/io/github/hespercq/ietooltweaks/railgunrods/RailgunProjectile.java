package io.github.hespercq.ietooltweaks.railgunrods;

import blusunrize.immersiveengineering.api.tool.RailgunHandler.RailgunRenderColors;

public record RailgunProjectile(int chargeDuration, double speed, double rodDamage, double rodGravity, boolean rodIsAbstractArrow, RailgunRenderColors rodColors ) {

}
