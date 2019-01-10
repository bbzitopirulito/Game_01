package com.gcstudios.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.gcstudios.main.Game;
import com.gcstudios.world.Camera;
import com.gcstudios.world.World;

public class Boss extends Enemy{
	
	private BufferedImage[] sprites;
	
	private int maskx = 16, masky = 16, maskw = 25, maskh = 25;
	//0.8
	private double speed = 0.8;
	
	private int frames = 0, maxFrames = 20, index = 0,maxIndex = 1;
	
	private boolean isDamaged = false;
	private int damageFrames = 10, damageCurrent = 0;

	private int life = 10;
	private int frames2 = 0,index2 = 0,maxIndex2 = 1;
	private BufferedImage[] sprites2;
	private int lvl2counter = 0;
	public static boolean lvl2blink = false;
	public Boss(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		sprites = new BufferedImage[2];
		sprites2 = new BufferedImage[2];
		sprites[0] = Game.spritesheet.getSprite(32, 32, 32, 32);
		sprites[1] = Game.spritesheet.getSprite(32*2, 32, 32, 32);
		sprites2[0] = sprites[0];
		sprites2[1] = Entity.BOSS_BLINK;
	}
	
	public void tick() {
		if(this.isColiddingWithPlayer() == false) {
			if((int)x < Game.player.x && World.isFree(12+(int)(x+speed), this.getY(), 16) 
					&& !this.isColidding((int)(x+speed), this.getY(), maskx, masky, maskw, maskh)) {
				x+=speed;
			}else if((int)x > Game.player.x && World.isFree((int)(x-speed) + 4, this.getY(), 16) 
					&& !this.isColidding((int)(x-speed), this.getY(), maskx, masky, maskw, maskh)) {
				x-=speed;
			}
			
			if((int)y < Game.player.y && World.isFree(this.getX(), (int)(y+speed) + 15, 16) 
					&& !this.isColidding(this.getX(), (int)(y+speed), maskx, masky, maskw, maskh)) {
				y+=speed;
			}else if((int)y > Game.player.y && World.isFree(this.getX(),(int)(y-speed) + 1, 16) 
					&& !this.isColidding(this.getX(), (int)(y-speed), maskx, masky, maskw, maskh)) {
				y-=speed;
			}
		}else {
			if(Game.rand.nextInt(100) < 10) {
				Game.player.isDamaged = true;
				int dan = 0;
				for(int i = 0; dan < 10 || dan > 15;i++) {
					dan = Game.rand.nextInt(16);			
				}
				Game.player.life -= dan;
			}
		}		
		
		if(Game.CUR_LEVEL == 2) {
			this.lvl2counter++;
			if(this.lvl2counter >= this.lvl2counterMAX) {
				lvl2blink = true;
				frames2++;
				if(frames2 == maxFrames2) {
					frames2 = 0;
					index2++;
					if(index2 > maxIndex2) {
						index2 = 0;
					}
				}
			}
		}else {
			lvl2blink = false;
		}
		
		if(Game.CUR_LEVEL == 2 && lvl2blink == false) {
			speed = 0;
		}else {
			speed = 0.8;
		}
		
		frames++;
		if(frames == maxFrames) {
			frames = 0;
			index++;
			if(index > maxIndex) {
				index = 0;
			}
		}
		
		this.collidingBullet();
		
		if(life <= 0) {
			this.destroySelf();
		}
		
		if(isDamaged) {
			this.damageCurrent++;
			if(damageCurrent == damageFrames) {
				isDamaged = false;
				damageCurrent = 0;
			}
		}
	}
	
	public void collidingBullet() {
		for(int i = 0; i < Game.bullets.size(); i++) {
			Entity e = Game.bullets.get(i);
			
			if(Entity.isColidding(this, e)) {
				isDamaged = true;
				life--;
				Game.bullets.remove(i);
				return;
			}			
		}
	}
	
	public void render(Graphics g) {
		if(this.lvl2blink) {
			g.drawImage(sprites2[index2], this.getX() - Camera.x, this.getY() - Camera.y, null);
		}else {
			if(!isDamaged) 
				g.drawImage(sprites[index], this.getX() - Camera.x, this.getY() - Camera.y, null);			
			else
				g.drawImage(Entity.BOSS_FEEDBACK, this.getX() - Camera.x, this.getY() - Camera.y, null);			
		}
		//g.setColor(Color.BLUE);
		//g.fillRect(this.getX() + maskx - Camera.x, this.getY() + masky - Camera.y, maskw, maskh);
	}
	
	
		

}
