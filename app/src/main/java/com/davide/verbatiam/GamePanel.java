package com.davide.verbatiam;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;

public class GamePanel extends Activity{
    //ProgressBar
    private ProgressBar life;
    //TextView
    private TextView gameover;
    private TextView coinView;
    private TextView coinViewAll;
    private TextView score2;
    //Image
    private ImageView resume;
    private ImageView restart;
    private ImageView exitM;
    private ImageView settings;
    //Layout
    private ConstraintLayout menu;
    private ConstraintLayout gioco;
    //Class
    private Runnable runnable;
    private Handler handler = new Handler();
    private Costants costants = new Costants();
    private Player player;
    private ShieldPlayer shieldPlayer;
    private Storage storage = new Storage();
    //Boolean
    private boolean handlerFlag = true;
    //timer
    private int lifeCount=0;
    private int damageEnemy=100;
    //Bullet
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private Handler handlerShoot = new Handler();
    private Runnable runnableShoot;
    //Enemy
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<EnemyLife> enemyLifes = new ArrayList<>();
    private ArrayList<ExplosionEnemy> explosionEnemies = new ArrayList<>();
    private ExplosionPlayer explosionPlayer;
    private Handler handlerEnemy = new Handler();
    private Runnable runnableEnemy;

    private Handler handlerCollisionEnemy = new Handler();
    private Runnable runnableCollisionEnemy;

    private Handler handlerCollisionBullets = new Handler();
    private Runnable runnableCollisionBullets;

    private int speedBullet = 15;
    private int contatoreTimer = 0;
    private int increaseEnemy = 3;
    private int increaseProgressBar = 3;
    private int coin = 0;

    private Handler handlerScore = new Handler();
    private Runnable runnableScore;

    private Handler handlerIncreaseEnemy = new Handler();
    private Runnable runnableIncreaseEnemy;

    private Handler handlerIncreaseProgress = new Handler();
    private Runnable runnableIncreaseProgress;

    private ArrayList<Shield> shields = new ArrayList<>();
    private Handler handlerShield = new Handler();
    private Runnable runnableShield;

    private Handler handlerBackground = new Handler();
    private Runnable runnableBackground;

    private DatabaseHelper db;

    private MediaPlayer explosionSound;
    private MediaPlayer lasershootSound;
    private MediaPlayer gameSong;
    private MediaPlayer gameOver;

    private boolean isTouch = true;

    long millis = 1000L;

    private int length;

    private int bulletG1 = 25; //x1
    private int bulletG2 = 50; //x1
    private int bulletG3 = 75; //x1

    private int bulletR1 = 25; //x2
    private int bulletR2 = 50; //x2
    private int bulletR3 = 100; //x2

    private int bulletU1 = 200; //x3

    private int maxLife = 25;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_gamepanel);

        DisplayMetrics ds = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(ds);
        costants.SCREEN_WIDTH = ds.widthPixels;
        costants.SCREEN_HEIGHT = ds.heightPixels;

        db = new DatabaseHelper(this);

        Cursor res = db.selectData();
        if(res.getCount() == 0) {
            db.insertData(1,storage.coinStorageF, storage.scoreT,1,0,0,1,0,0,1,0,0,0);
        }

        if(res.getCount() != 0) {
            while (res.moveToNext()) {
                //storage.coinStorageF = res.getLong(1);
                storage.green = res.getInt(3);
                storage.red = res.getInt(4);
                storage.ultimate = res.getInt(5);
                storage.g1 = res.getInt(6);
                storage.g2= res.getInt(7);
                storage.g3= res.getInt(8);
                storage.r1= res.getInt(9);
                storage.r2 = res.getInt(10);
                storage.r3= res.getInt(11);
            }
        }

        System.out.println("green " + storage.green);
        System.out.println("red " + storage.red);
        System.out.println("ultimate " + storage.ultimate);
        System.out.println("g1 " + storage.g1);
        System.out.println("g2 " + storage.g2);
        System.out.println("g3 " + storage.g3);
        System.out.println("r1 " + storage.r1);
        System.out.println("r2 " + storage.r2);
        System.out.println("r3 " + storage.r3);

        explosionSound = MediaPlayer.create(this,R.raw.explosion);
        lasershootSound = MediaPlayer.create(this,R.raw.lasershoot);
        gameSong = MediaPlayer.create(this,R.raw.game);
        gameOver = MediaPlayer.create(this,R.raw.gameover);
        lasershootSound.setVolume(0.0f,0.1f);
        explosionSound.setVolume(0.0f,0.3f);
        gameSong.setVolume(0.0f,0.5f);
        gameOver.setVolume(0.0f,1f);

        gameSong.start();
        gameSong.setLooping(true);

        //TextView
        score2 = (TextView) findViewById(R.id.score2);
        coinView = (TextView) findViewById(R.id.coinText);
        coinViewAll = (TextView) findViewById(R.id.coinAll);

        //Layout
        gioco = (ConstraintLayout) findViewById(R.id.gioco);

        //Set Player
        player = new Player(this, 400, 1500);
        gioco.addView(player, 1);

        //Set Shield
        shieldPlayer = new ShieldPlayer(this,325,1410);
        gioco.addView(shieldPlayer, 1);
        shieldPlayer.setVisibility(View.INVISIBLE);

        //Set ExplosionPlayer
        explosionPlayer = new ExplosionPlayer(this, 800, 850);
        gioco.addView(explosionPlayer, 1);

        //gameover
        gameover = (TextView) findViewById(R.id.gameover);

        //ProgressBar Life
        life = (ProgressBar) findViewById(R.id.life);
        if(storage.green == 2)
        {
            life.setMax(400);
            life.setProgress(400);
        }
        else if(storage.red == 2)
        {
            life.setMax(800);
            life.setProgress(800);
        }
        else if(storage.ultimate == 2)
        {
            life.setMax(1600);
            life.setProgress(1600);
        }



        //Comandi
        resume = (ImageView) findViewById(R.id.resume);
        restart = (ImageView) findViewById(R.id.restart);
        exitM = (ImageView) findViewById(R.id.exitM);
        settings = (ImageView) findViewById(R.id.settings);

        //Layout
        menu = (ConstraintLayout) findViewById(R.id.constraintLayoutPause);

        handlerScore.post(runnableScore = new Runnable() {
            @Override
            public void run() {
                timer();
                handlerScore.postDelayed(this, 30);
            }
        });

        handlerIncreaseProgress.post(runnableIncreaseProgress = new Runnable() {
            @Override
            public void run() {
                increaseProgressBar();
                handlerIncreaseProgress.postDelayed(this,30000);
            }
        });

        handlerIncreaseEnemy.post(runnableIncreaseEnemy = new Runnable() {
            @Override
            public void run() {
                handlerIncreaseEnemy.postDelayed(this,30000);
                increaseSpeedEnemy();
            }
        });

        handlerShoot.post(runnableShoot = new Runnable() {
            @Override
            public void run() {
                if(handlerFlag)
                {
                    spawnBullet();
                    deleteBullet();
                    handlerShoot.postDelayed(this,700);
                }
            }
        });

        handlerEnemy.post(runnableEnemy = new Runnable() {
            @Override
            public void run() {
                if(handlerFlag)
                {
                    spawnEnemy();
                    deleteEnemy();
                    deleteEnemyLife();
                    handlerEnemy.postDelayed(this,millis);
                }
            }
        });

        handlerCollisionEnemy.post(runnableCollisionEnemy = new Runnable() {
            @Override
            public void run() {
                hitCheckEnemyPlayer();
                hitCheckShieldPlayer();
                if(shieldPlayer.getVisibility() == View.VISIBLE)
                {
                    hitCheckShieldEnemy();
                }
                handlerCollisionEnemy.postDelayed(this, 1);
            }
        });

        handlerCollisionBullets.post(runnableCollisionBullets = new Runnable() {
            @Override
            public void run() {
                hitCheckBulletEnemy();
                coinView.setText(coin+"");
                handlerCollisionBullets.postDelayed(this,1);
            }
        });

        handlerShield.post(runnableShield = new Runnable() {
            @Override
            public void run() {
                spawnShield();
                deleteShield();
                handlerShield.postDelayed(this,120000);
            }
        });

        //Comando per entrare nel menu
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTouch = false;
                gameSong.pause();
                length=gameSong.getCurrentPosition();
                menu.setVisibility(View.VISIBLE);
                handler.removeCallbacks(runnable);
                handlerScore.removeCallbacks(runnableScore);
                handlerEnemy.removeCallbacks(runnableEnemy);
                handlerShoot.removeCallbacks(runnableShoot);

                for(Enemy enemy: enemies)
                {
                    for (ExplosionEnemy explosionEnemy: explosionEnemies)
                    {
                        for(EnemyLife enemyLife: enemyLifes)
                        {
                            enemyLife.setSpeedY(0);
                            enemy.setSpeedY(0);
                            explosionEnemy.setSpeedY(0);
                        }
                    }

                }
                for(Shield shield : shields)
                {
                    shield.setSpeedY(0);
                }

                for(Bullet bullet: bullets)
                {
                    bullet.setSpeedY(0);
                }
            }
        });

        //Comando per riprendere il gioco
        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTouch = true;
                gameSong.seekTo(length);
                gameSong.start();
                menu.setVisibility(View.INVISIBLE);
                handler.postDelayed(runnable, 20);
                handlerScore.postDelayed(runnableScore,30);
                handlerEnemy.postDelayed(runnableEnemy, 20);
                handlerShoot.postDelayed(runnableShoot, 20);

                for(Enemy enemy: enemies)
                {
                    for (ExplosionEnemy explosionEnemy: explosionEnemies)
                    {
                        for(EnemyLife enemyLife: enemyLifes)
                        {
                            enemyLife.setSpeedY(increaseProgressBar);
                            enemy.setSpeedY(increaseEnemy);
                            explosionEnemy.setSpeedY(increaseEnemy);
                        }
                    }

                }
                for(Shield shield : shields)
                {
                    shield.setSpeedY(increaseEnemy);
                }

                for(Bullet bullet: bullets)
                {
                    bullet.setSpeedY(speedBullet);
                }
            }
        });

        //Comando per fare un restart del gioco
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameSong.stop();
                gameSong.reset();
                finish();
                startActivity(getIntent());
            }
        });

        //Comando per uscire dal gioco
        exitM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(runnable);
                handlerEnemy.removeCallbacks(runnableEnemy);
                handlerShoot.removeCallbacks(runnableShoot);
                handlerScore.removeCallbacks(runnableScore);
                finishAffinity();
                Intent intent = new Intent(GamePanel.this, MainActivity.class);
                startActivity(intent);
                System.exit(0);
            }
        });

        handlerBackground.post(runnableBackground = new Runnable() {
            @Override
            public void run() {
                backgroundScrool();
                handlerBackground.postDelayed(this, 50);
            }
        });

        player.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isTouch) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            break;
                        case MotionEvent.ACTION_MOVE:
                            int xPosPlayer = (int) event.getRawX() - player.getWidth() / 2;
                            int yPosPlayer = (int) (event.getRawY() - player.getHeight() / 2) - 150;
                            player.setX(xPosPlayer);
                            player.setY(yPosPlayer);
                            player.setRect(xPosPlayer, yPosPlayer, xPosPlayer, yPosPlayer);
                            int xPosShield = (int) event.getRawX() - shieldPlayer.getWidth() / 2;
                            int yPosShield = (int) (event.getRawY() - shieldPlayer.getHeight() / 2) - 150;
                            shieldPlayer.setX(xPosShield);
                            shieldPlayer.setY(yPosShield);
                            shieldPlayer.setRect(xPosShield, yPosShield, xPosShield, yPosShield);
                            return true;
                        case MotionEvent.ACTION_UP:
                            return false;
                    }
                }
                return true;
            }
        });
    }


    @Override
    public void onBackPressed() {

        gameSong.pause();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Sei sicuro di voler uscire dal gioco?");

        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                System.exit(0);
            }
        });

        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gameSong.seekTo(length);
                gameSong.start();
            }
        });
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.show();
    }

    public void backgroundScrool()
    {
        ImageView backgroundI = (ImageView) findViewById(R.id.img);
        if(backgroundI.getY() < -6300)
        {
            backgroundI.setY(0);
        }
        else
        {
            backgroundI.setY(backgroundI.getY() - 10);
        }
    }

    public void timer()
    {
        contatoreTimer = contatoreTimer + 1;
        score2.setText(contatoreTimer+"");

        if(contatoreTimer >= 500)
        {
            for(Enemy enemy: enemies)
            {
                for (ExplosionEnemy explosionEnemy: explosionEnemies)
                {
                    enemy.setSpeedY(increaseEnemy);
                    explosionEnemy.setSpeedY(increaseEnemy);
                }
            }
            for(Shield shield : shields)
            {
                shield.setSpeedY(increaseEnemy);
            }

            for(EnemyLife enemyLife: enemyLifes)
            {
                enemyLife.setSpeedY(increaseProgressBar);
            }
        }
    }

    public void increaseProgressBar()
    {
        maxLife = maxLife + 25;

        for(EnemyLife enemyLife: enemyLifes)
        {
            enemyLife.setMax(maxLife);
            enemyLife.setProgress(maxLife);
            System.out.println("Max "+enemyLife.getMax());
            System.out.println("Progress "+enemyLife.getProgress());
        }
    }

    public void increaseSpeedEnemy()
    {
        increaseProgressBar = increaseProgressBar + 1;
        increaseEnemy = increaseEnemy + 1;
        damageEnemy = damageEnemy + 20;


        if(millis == 300)
        {
            millis = 300;
        }
        else
        {
            millis = millis - 50;
        }
    }

    public void hitCheckBulletEnemy()
    {
        //Spawn oggetto esplosione
        ArrayList<Enemy> deleteEnemy = new ArrayList<>();
        ArrayList<Bullet> deleteBullet = new ArrayList<>();
        ArrayList<EnemyLife> deleteEnemyLife = new ArrayList<>();

        for(Bullet bullet: bullets)
        {
            for(final Enemy enemy: enemies)
            {
                for(EnemyLife enemyLife: enemyLifes)
                {
                    if (bullet.collide(enemy))
                    {
                        deleteBullet.add(bullet);

                        if(enemyLife.getX() == enemy.getX() || enemyLife.getY() == enemy.getY())
                        {
                            if(storage.g1 == 2)
                            {
                                enemyLife.setProgress(enemyLife.getProgress() - bulletG1);
                                deleteEnemyLife.add(enemyLife);
                                deleteEnemy.add(enemy);
                            }
                            if(storage.g2 == 2)
                            {
                                enemyLife.setProgress(enemyLife.getProgress() - bulletG2);
                                deleteEnemyLife.add(enemyLife);
                                deleteEnemy.add(enemy);
                            }
                            if(storage.g3 == 2)
                            {
                                enemyLife.setProgress(enemyLife.getProgress() - bulletG3);
                                deleteEnemyLife.add(enemyLife);
                                deleteEnemy.add(enemy);
                            }
                            if(storage.r1 == 2)
                            {
                                enemyLife.setProgress(enemyLife.getProgress() - bulletR1);
                                deleteEnemyLife.add(enemyLife);
                                deleteEnemy.add(enemy);
                            }
                            if(storage.r2 == 2)
                            {
                                enemyLife.setProgress(enemyLife.getProgress() - bulletR2);
                                deleteEnemyLife.add(enemyLife);
                                deleteEnemy.add(enemy);
                            }
                            if(storage.r3 == 2)
                            {
                                enemyLife.setProgress(enemyLife.getProgress() - bulletR3);
                                deleteEnemyLife.add(enemyLife);
                                deleteEnemy.add(enemy);
                            }
                            if(storage.ultimate == 2)
                            {
                                enemyLife.setProgress(enemyLife.getProgress() - bulletU1);
                                deleteEnemyLife.add(enemyLife);
                                deleteEnemy.add(enemy);
                            }
                        }
                        if(enemyLife.getProgress()<=0)
                        {
                            coin = coin + 2;
                            for(final ExplosionEnemy explosionEnemy : explosionEnemies)
                            {
                                explosionEnemy.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(explosionEnemy.getX() == enemy.getX() || explosionEnemy.getY() == enemy.getY())
                                        {
                                            explosionSound.start();
                                            explosionEnemy.setVisibility(View.VISIBLE);
                                            explosionEnemy.startAnimation();
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }

        for(Bullet bullet: deleteBullet)
        {
            for(Enemy enemy: deleteEnemy)
            {
                bullets.remove(bullet);
                gioco.removeView(bullet);
                System.out.println(deleteBullet.size());
                for(EnemyLife enemyLife: deleteEnemyLife)
                {
                    if(enemyLife.getProgress() <= 0)
                    {
                        enemy.stopHandler();
                        bullet.stopHandler();
                        enemyLife.stopHandler();
                        enemies.remove(enemy);
                        enemyLifes.remove(enemyLife);
                        gioco.removeView(enemyLife);
                        gioco.removeView(enemy);
                    }
                }
            }
        }
    }

    public void hitCheckShieldPlayer() {
        //Spawn oggetto esplosione
        ArrayList<Shield> deleteShield = new ArrayList<>();

        for (Shield shield : shields) {

            if (shield.collide(player))
            {
                shield.setVisibility(View.VISIBLE);
                shieldPlayer.setVisibility(View.VISIBLE);
                deleteShield.add(shield);
            }
        }

        for (Shield shield : deleteShield) {
            shields.remove(shield);
            gioco.removeView(shield);
        }
    }

    public void hitCheckShieldEnemy() {

        //Spawn oggetto esplosione
        ArrayList<Enemy> deleteEnemy = new ArrayList<>();
        ArrayList<EnemyLife> deleteEnemyLife = new ArrayList<>();

        for(final Enemy enemy: enemies)
        {
            if (enemy.collideShield(shieldPlayer))
            {
                for(EnemyLife enemyLife: enemyLifes)
                {
                    if (enemyLife.getX() == enemy.getX() || enemyLife.getY() == enemy.getY()) {
                        deleteEnemyLife.add(enemyLife);
                        shieldPlayer.setVisibility(View.INVISIBLE);
                        deleteEnemy.add(enemy);
                    }
                }
            }
        }

        for (Enemy enemy : deleteEnemy)
        {
            for(EnemyLife enemyLife: deleteEnemyLife)
            {
                enemies.remove(enemy);
                gioco.removeView(enemy);

                enemyLifes.remove(enemyLife);
                gioco.removeView(enemyLife);
            }

        }
    }

    public void hitCheckEnemyPlayer()
    {
        ArrayList<Enemy> deleteCollision = new ArrayList<>();
        ArrayList<EnemyLife> deleteEnemyLife = new ArrayList<>();

        for(final Enemy enemy: enemies)
        {
            if(enemy.collide(player))
            {
                for(EnemyLife enemyLife: enemyLifes)
                {
                    if (enemyLife.getX() == enemy.getX() || enemyLife.getY() == enemy.getY()) {
                        deleteEnemyLife.add(enemyLife);
                    }
                }
                for(final ExplosionEnemy explosionEnemy : explosionEnemies)
                {

                    explosionEnemy.post(new Runnable() {
                        @Override
                        public void run() {
                            if(explosionEnemy.getX() == enemy.getX() || explosionEnemy.getY() == enemy.getY())
                            {
                                explosionSound.start();
                                explosionEnemy.setVisibility(View.VISIBLE);
                                explosionEnemy.startAnimation();
                            }

                        }
                    });
                }
                deleteCollision.add(enemy);
                life.setProgress(life.getProgress()- damageEnemy);
            }
        }

        for(Enemy enemy : deleteCollision)
        {
            for(EnemyLife enemyLife: deleteEnemyLife)
            {
                enemy.stopHandler();
                enemies.remove(enemy);
                gioco.removeView(enemy);
                enemyLifes.remove(enemyLife);
                gioco.removeView(enemyLife);
                if(life.getProgress() <= 0)
                {
                    gameSong.stop();
                    gameOver.start();
                    gioco.removeView(player);
                    explosionPlayer.setVisibility(View.VISIBLE);
                    explosionPlayer.startAnimation();
                    handlerScore.removeCallbacks(runnableScore);
                    handler.removeCallbacks(runnable);
                    handlerShoot.removeCallbacks(runnableShoot);
                    handlerEnemy.removeCallbacks(runnableEnemy);
                    handlerCollisionEnemy.removeCallbacks(runnableCollisionEnemy);
                    handlerCollisionBullets.removeCallbacks(runnableCollisionBullets);
                    gameover.setVisibility(View.VISIBLE);
                    storage.coinStorageI = storage.coinStorageI + coin;
                    storage.coinStorageF = storage.coinStorageF + coin;
                    storage.scoreT = contatoreTimer;

                    Cursor res = db.selectData();
                    if(res.getCount() != 0)
                    {
                        db.updateData(storage.coinStorageF+10000);
                        db.updateScore(storage.scoreT);
                    }

                    String risultato="";

                    while (res.moveToNext()) {
                        risultato = "Coin Totali " + res.getString(1) +"\nNe hai ottenuti "+ storage.coinStorageI;
                    }

                    coinViewAll.setText(risultato);
                }
            }

        }

    }

    public void spawnEnemy()
    {
        float enemyX = (float) Math.floor(Math.random() * (costants.SCREEN_WIDTH - 150));
        float enemyY = -100;

        //Spawn oggetto enemy
        Enemy enemy = new Enemy(this,enemyX,enemyY);
        enemies.add(enemy);
        gioco.addView(enemy, 1);

        //Spawn oggetto progressBar
        EnemyLife enemyLife = new EnemyLife(this,null, android.R.attr.progressBarStyleHorizontal,enemyX, enemyY, maxLife);
        enemyLifes.add(enemyLife);
        gioco.addView(enemyLife, 1);

        ExplosionEnemy explosionEnemy = new ExplosionEnemy(this,enemyX,enemyY);
        explosionEnemies.add(explosionEnemy);
        gioco.addView(explosionEnemy, 1);
    }

    public void spawnShield()
    {
        float shieldX = (float) Math.floor(Math.random() * (costants.SCREEN_WIDTH - 150));
        float shieldY = -100;

        //Spawn oggetto shield
        Shield shield = new Shield(this, shieldX,shieldY);
        shields.add(shield);
        gioco.addView(shield,1);
    }

    public void deleteShield()
    {
        ArrayList<Shield> delete = new ArrayList<>();
        for(Shield shield : shields)
        {
            if(shield.getY() > costants.SCREEN_HEIGHT)
            {
                delete.add(shield);
            }
        }
        for(Shield shield : delete)
        {
            shield.stopHandler();
            shields.remove(shield);
            gioco.removeView(shield);
        }

    }

    public void deleteEnemy()
    {
        ArrayList<Enemy> delete = new ArrayList<>();

        for(Enemy enemy : enemies)
        {
            if(enemy.getY() > costants.SCREEN_HEIGHT)
            {
                delete.add(enemy);
            }
        }

        for(Enemy enemy : delete)
        {
            enemy.stopHandler();
            enemies.remove(enemy);
            gioco.removeView(enemy);
        }
    }

    public void deleteEnemyLife()
    {
        ArrayList<EnemyLife> deleteEnemyLife = new ArrayList<>();

        for(EnemyLife enemyLife : enemyLifes)
        {
            if(enemyLife.getY() > costants.SCREEN_HEIGHT)
            {
                deleteEnemyLife.add(enemyLife);
            }
        }

        for(EnemyLife enemyLife : deleteEnemyLife)
        {
            enemyLife.stopHandler();
            enemyLifes.remove(enemyLife);
            gioco.removeView(enemyLife);
        }
    }

    public void spawnBullet()
    {
        /*System.out.println("Green: " + storage.g1 + storage.g2 + storage.g3);
        System.out.println("Red: " + storage.r1 + storage.r2 + storage.r3);
        System.out.println("Ultimate: " + storage.ultimate);
        System.out.println("Tot: " + storage.green + storage.red + storage.ultimate);*/
        if(storage.g1 == 2 || storage.g2 == 2 || storage.g3 == 2)
        {
            Bullet bulletG = new Bullet(this,player.getX()+177, player.getY());
            bullets.add(bulletG);
            gioco.addView(bulletG,1);
            if(storage.g1 == 2)
            {
                Drawable drawable = getResources().getDrawable(R.drawable.bullets);
                bulletG.setImageDrawable(drawable);
                bulletG.setMillis(20);
            }
            if(storage.g2 == 2)
            {
                Drawable drawable = getResources().getDrawable(R.drawable.bulletm);
                bulletG.setImageDrawable(drawable);
                bulletG.setMillis(20);
            }
            if(storage.g3 == 2)
            {
                Drawable drawable = getResources().getDrawable(R.drawable.bulleth);
                bulletG.setImageDrawable(drawable);
                bulletG.setMillis(20);
                bulletG.setLayoutParams(new ConstraintLayout.LayoutParams(45, 75));
            }
        }
        else if(storage.r1 == 2 || storage.r2 == 2 || storage.r3 == 2)
        {
            Bullet bulletR1 = new Bullet(this,player.getX()+110, player.getY()+20);
            Bullet bulletR2 = new Bullet(this,player.getX()+250, player.getY()+20);
            bullets.add(bulletR1);
            bullets.add(bulletR2);
            gioco.addView(bulletR1,1);
            gioco.addView(bulletR2,1);
            if(storage.r1 == 2)
            {
                Drawable drawable = getResources().getDrawable(R.drawable.bullets);
                bulletR1.setImageDrawable(drawable);
                bulletR2.setImageDrawable(drawable);
                bulletR1.setMillis(15);
                bulletR2.setMillis(15);
            }
            if(storage.r2 == 2)
            {
                Drawable drawable = getResources().getDrawable(R.drawable.bulletm);
                bulletR1.setImageDrawable(drawable);
                bulletR2.setImageDrawable(drawable);
                bulletR1.setMillis(15);
                bulletR2.setMillis(15);
            }
            if(storage.r3 == 2)
            {
                Drawable drawable = getResources().getDrawable(R.drawable.bulleth);
                bulletR1.setImageDrawable(drawable);
                bulletR2.setImageDrawable(drawable);
                bulletR1.setMillis(15);
                bulletR2.setMillis(15);
                bulletR1.setLayoutParams(new ConstraintLayout.LayoutParams(45, 75));
                bulletR2.setLayoutParams(new ConstraintLayout.LayoutParams(45, 75));
            }
        }
        else if(storage.ultimate == 2)
        {
            Bullet bulletU1 = new Bullet(this,player.getX()+10, player.getY()+50);
            Bullet bulletU2 = new Bullet(this,player.getX()+145, player.getY());
            Bullet bulletU3 = new Bullet(this,player.getX()+280, player.getY()+50);
            bullets.add(bulletU1);
            bullets.add(bulletU2);
            bullets.add(bulletU3);
            gioco.addView(bulletU1,1);
            gioco.addView(bulletU2,1);
            gioco.addView(bulletU3,1);
            Drawable drawable = getResources().getDrawable(R.drawable.bulleth);
            bulletU1.setImageDrawable(drawable);
            bulletU2.setImageDrawable(drawable);
            bulletU3.setImageDrawable(drawable);
            bulletU1.setLayoutParams(new ConstraintLayout.LayoutParams(45, 75));
            bulletU2.setLayoutParams(new ConstraintLayout.LayoutParams(45, 75));
            bulletU3.setLayoutParams(new ConstraintLayout.LayoutParams(45, 75));
            bulletU1.setMillis(9);
            bulletU2.setMillis(9);
            bulletU3.setMillis(9);
        }
        lasershootSound.start();
    }

    public void deleteBullet()
    {
        ArrayList<Bullet> delete = new ArrayList<>();
        for(Bullet bullet: bullets)
        {
            if(bullet.getY() < - bullet.getHeight())
            {
                delete.add(bullet);
            }
        }
        for(Bullet bullet : delete)
        {
            bullet.stopHandler();
            bullets.remove(bullet);
            gioco.removeView(bullet);
        }
    }
}