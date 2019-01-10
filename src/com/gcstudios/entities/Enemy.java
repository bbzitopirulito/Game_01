package com.gcstudios.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.gcstudios.main.Game;
import com.gcstudios.world.Camera;
import com.gcstudios.world.World;

public class Enemy extends Entity{

	private double speed = 0.4;
										    //10        //10
	private int maskx = 8, masky = 8, maskw = 10, maskh = 10;
	
	private int frames = 0, maxFrames = 20, index = 0,maxIndex = 1;
	
	private BufferedImage[] sprites;
	
	
	public static final int maxFrames2 = 14;
	public static final int lvl2counterMAX = 1595; 
	private int frames2 = 0,index2 = 0,maxIndex2 = 1;
	private BufferedImage[] sprites2;
	private int lvl2counter = 0;
	private boolean lvl2blink = false;
	
	//10
	private int life = 1;
	
	private boolean isDamaged = false;
	private int damageFrames = 10, damageCurrent = 0;
	
	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, null);		
		
		sprites = new BufferedImage[2];
		sprites2 = new BufferedImage[2];
		sprites[0] = Game.spritesheet.getSprite(112, 16, World.TILE_SIZE, World.TILE_SIZE);
		sprites[1] = Game.spritesheet.getSprite(112+16, 16, World.TILE_SIZE, World.TILE_SIZE);
		sprites2[0] = sprites[0];
		sprites2[1] = Entity.ENEMY_BLINK;
	}
	
	public void tick() {
		if(isColiddingWithPlayer() == false) {
			if((int)x < Game.player.getX() && World.isFree((int)(x+speed), this.getY(), World.TILE_SIZE)
					&& !isColidding((int)(x+speed), this.getY(), maskx, masky, maskw, maskh)) {
				x+=speed;
			}else if((int)x > Game.player.getX() && World.isFree((int)(x-speed), this.getY(), World.TILE_SIZE)
					&& !isColidding((int)(x-speed), this.getY(), maskx, masky, maskw, maskh)){
				x-=speed;
			}
			
			if((int)y < Game.player.getY() && World.isFree(this.getX(), (int)(y+speed), World.TILE_SIZE)
					&& !isColidding(this.getX(), (int)(y+speed), maskx, masky, maskw, maskh)) {
				y+=speed;
			}else if((int)y > Game.player.getY() && World.isFree(this.getX(), (int)(y-speed), World.TILE_SIZE)
					&& !isColidding(this.getX(), (int)(y-speed), maskx, masky, maskw, maskh)){
				y-=speed;
			}
		}else {
			//estamos colidindo
			if(Game.rand.nextInt(100) < 10) {
				Game.player.life -= Game.rand.nextInt(3);
				Game.player.isDamaged = true;
				
				//System.out.println("Vida: " + Game.player.life);
			}					
		}
		
		frames++;
		if(frames == maxFrames) {
			frames = 0;
			index++;
			if(index > maxIndex) {
				index = 0;
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
			speed = 0.4;
		}
		
		collidingBullet();
		
		if(life <= 0) {
			destroySelf();
		}
		
		if(isDamaged) {
			this.damageCurrent++;
			if(this.damageCurrent == this.damageFrames) {
				this.damageCurrent = 0;
				isDamaged = false;
			}
		}
		
	}
		
	
	public void destroySelf() {
		Game.enemies.remove(this);
		Game.entities.remove(this);
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
	
	public boolean isColiddingWithPlayer() {
		Rectangle enemyCurrent = new Rectangle(this.getX() + maskx,this.getY() + masky, maskw,maskh);
		Rectangle player = new Rectangle(Game.player.getX(),Game.player.getY(), 16,16);
		
		return enemyCurrent.intersects(player);
	}
	
	public boolean isColidding(int xnext, int ynext,int maskx, int masky, int maskw, int maskh) {
		Rectangle enemyCurrent = new Rectangle(xnext + maskx,ynext + masky, maskw,maskh);
		
		for(int i = 0; i < Game.enemies.size();i++) {
			Enemy e = Game.enemies.get(i);
			if(e == this)
				continue;
			Rectangle targetEnemy = new Rectangle(e.getX() + maskx,e.getY() + masky, maskw,maskh);
			if(targetEnemy.intersects(enemyCurrent)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void render(Graphics g) {
		if(this.lvl2blink) {
			g.drawImage(sprites2[index2], this.getX() - Camera.x, this.getY() - Camera.y, null);
		}else {
			if(!isDamaged)
				g.drawImage(sprites[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
			else 
				g.drawImage(Entity.ENEMY_FEEDBACK, this.getX() - Camera.x, this.getY() - Camera.y, null);
		}

		//g.setColor(Color.BLUE);
		//g.fillRect(this.getX() + maskx - Camera.x, this.getY() + masky - Camera.y, maskw, maskh);
	}
	
}
