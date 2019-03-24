package com.davide.verbatiam;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Region;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Bundle;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class GamePanel extends Activity implements SensorEventListener {
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
    private ImageView option;
    private ImageView exitM;
    private ImageView settings;
    private ImageView homeB;
    //Immagini per le freccie
    private ImageView left;
    private ImageView right;
    //Layout
    private ConstraintLayout menu;
    private ConstraintLayout optionsMenu;
    private ConstraintLayout gioco;
    //ToggleButton
    private ToggleButton accelerometer;
    //Class
    private Runnable runnable;
    private Handler handler = new Handler();
    private Costants costants = new Costants();
    private Player player;
    private ShieldPlayer shieldPlayer;
    private Storage storage = new Storage();
    //Boolean
    private boolean rect_actionFlagDX = false;
    private boolean rect_actionFlagSX = false;
    private boolean handlerFlag = true;
    private boolean touchFlag = true;
    private boolean accelerometerFlag = false;
    private boolean menuFlag = true;
    //Sensor
    private Sensor sensor;
    private SensorManager sensorManager;
    //timer
    private int lifeCount=0;
    private int damageEnemy=100;
    //Bullet
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private Handler handlerShoot = new Handler();
    private Runnable runnableShoot;
    //Enemy
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<ExplosionEnemy> explosionEnemies = new ArrayList<>();
    private ExplosionPlayer explosionPlayer;
    private Handler handlerEnemy = new Handler();
    private Runnable runnableEnemy;

    private Handler handlerCollisionEnemy = new Handler();
    private Runnable runnableCollisionEnemy;

    private Handler handlerCollisionBullets = new Handler();
    private Runnable runnableCollisionBullets;

    private Handler handlerSpawnExplosion = new Handler();
    private Runnable runnableSpawnExplosion;

    private TextView score;
    private int contatore = 0;
    private int increaseEnemy = 3;
    private int coin = 0;

    private Handler handlerScore = new Handler();
    private Runnable runnableScore;

    private Handler handlerIncreaseEnemy = new Handler();
    private Runnable runnableIncreaseEnemy;

    private ArrayList<Shield> shields = new ArrayList<>();
    private Handler handlerShield = new Handler();
    private Runnable runnableShield;

    private Handler prova = new Handler();
    private Runnable provaRunnable;

    private DatabaseHelper db;

    private MediaPlayer explosionSound;
    private MediaPlayer lasershootSound;
    private MediaPlayer gameSong;
    private MediaPlayer gameOver;

    private boolean coinFlag = false;
    long millis = 1000L;

    private int length;

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
        if(res.getCount() != 0) {
            while (res.moveToNext()) {
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
        score = (TextView) findViewById(R.id.score);
        score2 = (TextView) findViewById(R.id.score2);
        coinView = (TextView) findViewById(R.id.coinText);
        coinViewAll = (TextView) findViewById(R.id.coinAll);

        //Layout
        gioco = (ConstraintLayout) findViewById(R.id.gioco);

        //Set Player
        player = new Player(this, 400, 1500);
        gioco.addView(player);

        //Set Shield
        shieldPlayer = new ShieldPlayer(this,325,1410);
        gioco.addView(shieldPlayer);
        shieldPlayer.setVisibility(View.INVISIBLE);

        explosionPlayer = new ExplosionPlayer(this, 800, 850);
        gioco.addView(explosionPlayer);

        //gameover
        gameover = (TextView) findViewById(R.id.gameover);

        //ProgressBar Life
        life = (ProgressBar) findViewById(R.id.life);
        life.setMax(300);
        life.setProgress(300);

        //Comandi
        resume = (ImageView) findViewById(R.id.resume);
        restart = (ImageView) findViewById(R.id.restart);
        option = (ImageView) findViewById(R.id.option);
        exitM = (ImageView) findViewById(R.id.exitM);
        settings = (ImageView) findViewById(R.id.settings);
        homeB = (ImageView) findViewById(R.id.home);

        //Layout
        menu = (ConstraintLayout) findViewById(R.id.constraintLayout1);
        optionsMenu = (ConstraintLayout) findViewById(R.id.constraintLayout2);

        //Switch
        accelerometer = (ToggleButton) findViewById(R.id.accelerometer);


        //Porzione di schermo da toccare
        left = (ImageView) findViewById(R.id.left);
        right = (ImageView) findViewById(R.id.right);

        //Manage Sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        handlerScore.post(runnableScore = new Runnable() {
            @Override
            public void run() {
                timer();
                handlerScore.postDelayed(this, 30);
            }
        });

        handlerIncreaseEnemy.post(runnableIncreaseEnemy = new Runnable() {
            @Override
            public void run() {
                handlerIncreaseEnemy.postDelayed(this,20000);
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
                gameSong.pause();
                length=gameSong.getCurrentPosition();
                menu.setVisibility(View.VISIBLE);
                handler.removeCallbacks(runnable);
                handlerScore.removeCallbacks(runnableScore);
                handlerEnemy.removeCallbacks(runnableEnemy);
                handlerShoot.removeCallbacks(runnableShoot);
            }
        });

        //Comando per riprendere il gioco
        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameSong.seekTo(length);
                gameSong.start();
                menu.setVisibility(View.INVISIBLE);
                menuFlag = true;
                handler.postDelayed(runnable, 20);
                handlerScore.postDelayed(runnableScore,30);
                handlerEnemy.postDelayed(runnableEnemy, 20);
                handlerShoot.postDelayed(runnableShoot, 20);
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
                coinFlag = true;
                handler.removeCallbacks(runnable);
                handlerEnemy.removeCallbacks(runnableEnemy);
                handlerShoot.removeCallbacks(runnableShoot);
                handlerScore.removeCallbacks(runnableScore);
                finish();
                System.exit(0);
            }
        });

        //Comando per entrare nel menu option
        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuFlag = false;
                handler.removeCallbacks(runnable);
                handlerEnemy.removeCallbacks(runnableEnemy);
                handlerShoot.removeCallbacks(runnableShoot);
                handlerScore.removeCallbacks(runnableScore);
                optionsMenu.setVisibility(View.VISIBLE);
                menu.setVisibility(View.INVISIBLE);
            }
        });

        //Comando per attivare l'accelerometro
        accelerometer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    touchFlag = false;
                    accelerometerFlag = true;
                    Toast.makeText(getBaseContext(),"On Accelerometer", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    touchFlag = true;
                    accelerometerFlag = false;
                    Toast.makeText(getBaseContext(),"On TouchEvent", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Comando per ritornare al menu del gioco
        homeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(runnable);
                handlerEnemy.removeCallbacks(runnableEnemy);
                handlerShoot.removeCallbacks(runnableShoot);
                handlerScore.removeCallbacks(runnableScore);
                optionsMenu.setVisibility(View.INVISIBLE);
                menu.setVisibility(View.VISIBLE);
            }
        });

        prova.post(provaRunnable = new Runnable() {
            @Override
            public void run() {
                backgroundScrool();
                prova.postDelayed(this, 50);
            }
        });


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
        contatore = contatore + 1;
        score2.setText(contatore+"");

        if(contatore >= 500)
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
        }
    }

    public void increaseSpeedEnemy()
    {
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

        for(Bullet bullet: bullets)
        {
            for(final Enemy enemy: enemies) {

                if (bullet.collide(enemy))
                {
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
                    coin = coin + 2;
                    deleteBullet.add(bullet);
                    deleteEnemy.add(enemy);
                }
            }
        }

        for(Bullet bullet: deleteBullet)
        {
            for(Enemy enemy: deleteEnemy)
            {
                enemy.stopHandler();
                bullet.stopHandler();
                enemies.remove(enemy);
                bullets.remove(bullet);
                gioco.removeView(enemy);
                gioco.removeView(bullet);
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

        for(final Enemy enemy: enemies)
        {
            if (enemy.collideShield(shieldPlayer))
            {
                shieldPlayer.setVisibility(View.INVISIBLE);
                deleteEnemy.add(enemy);
            }
        }

        for (Enemy enemy : deleteEnemy) {
            enemies.remove(enemy);
            gioco.removeView(enemy);
        }
    }

    public void hitCheckEnemyPlayer()
    {
        ArrayList<Enemy> deleteCollision = new ArrayList<>();

        for(final Enemy enemy: enemies)
        {
            if(enemy.collide(player))
            {
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
                lifeCount++;
                deleteCollision.add(enemy);
                if(life.getProgress() <= 200 && life.getProgress() > 100)
                {
                    life.getProgressDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
                }
                else if(life.getProgress() <= 100 && life.getProgress() >= 0)
                {
                    life.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                }
                life.setProgress(life.getProgress()-damageEnemy);
            }
        }

        for(Enemy enemy : deleteCollision)
        {
            enemy.stopHandler();
            enemies.remove(enemy);
            gioco.removeView(enemy);
            if(lifeCount==3)
            {
                enemies.remove(enemy);
                gioco.removeView(enemy);
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
                storage.coinStorageI = coin;
                storage.coinStorageF = storage.coinStorageF + storage.coinStorageI;
                storage.scoreT = contatore;

                Cursor res = db.selectData();
                if(res.getCount() == 0) {
                    db.insertData(1,storage.coinStorageF, storage.scoreT,1,0,0,1,0,0,1,0,0,0);
                }
                else
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

    public void spawnEnemy()
    {
        float enemyX = (float) Math.floor(Math.random() * (costants.SCREEN_WIDTH - 150));
        float enemyY = -100;

        //Spawn oggetto enemy
        Enemy enemy = new Enemy(this,enemyX,enemyY);
        enemies.add(enemy);
        gioco.addView(enemy);

        ExplosionEnemy explosionEnemy = new ExplosionEnemy(this,enemyX,enemyY);
        explosionEnemies.add(explosionEnemy);
        gioco.addView(explosionEnemy);
    }

    public void spawnShield()
    {
        float shieldX = (float) Math.floor(Math.random() * (costants.SCREEN_WIDTH - 150));
        float shieldY = -100;

        //Spawn oggetto shield
        Shield shield = new Shield(this, shieldX,shieldY);
        shields.add(shield);
        gioco.addView(shield);
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

    public void spawnBullet()
    {
        if(storage.g1 == 2 || storage.g2 == 2 || storage.g3 == 2)
        {
            Bullet bulletG = new Bullet(this,player.getX()+177, player.getY());
            bullets.add(bulletG);
            gioco.addView(bulletG);
            if(storage.g1 == 2)
            {
                Drawable drawable = getResources().getDrawable(R.drawable.bullets);
                bulletG.setImageDrawable(drawable);
            }
            if(storage.g2 == 2)
            {
                Drawable drawable = getResources().getDrawable(R.drawable.bulletm);
                bulletG.setImageDrawable(drawable);
            }
            if(storage.g3 == 2)
            {
                Drawable drawable = getResources().getDrawable(R.drawable.bulleth);
                bulletG.setImageDrawable(drawable);
            }
        }
        else if(storage.r1 == 2 || storage.r2 == 2 || storage.r3 == 2)
        {
            Bullet bulletR1 = new Bullet(this,player.getX()+110, player.getY()+20);
            Bullet bulletR2 = new Bullet(this,player.getX()+250, player.getY()+20);
            bullets.add(bulletR1);
            bullets.add(bulletR2);
            gioco.addView(bulletR1);
            gioco.addView(bulletR2);
            if(storage.r1 == 2)
            {
                Drawable drawable = getResources().getDrawable(R.drawable.bullets);
                bulletR1.setImageDrawable(drawable);
                bulletR2.setImageDrawable(drawable);
            }
            if(storage.r2 == 2)
            {
                Drawable drawable = getResources().getDrawable(R.drawable.bulletm);
                bulletR1.setImageDrawable(drawable);
                bulletR2.setImageDrawable(drawable);
            }
            if(storage.r3 == 2)
            {
                Drawable drawable = getResources().getDrawable(R.drawable.bulleth);
                bulletR1.setImageDrawable(drawable);
                bulletR2.setImageDrawable(drawable);
            }
        }
        else if(storage.ultimate == 2)
        {
            Bullet bulletU1 = new Bullet(this,player.getX()+80, player.getY()+20);
            Bullet bulletU2 = new Bullet(this,player.getX()+500, player.getY()+20);
            bullets.add(bulletU1);
            bullets.add(bulletU2);
            gioco.addView(bulletU1);
            gioco.addView(bulletU2);
            Drawable drawable = getResources().getDrawable(R.drawable.bulleth);
            bulletU1.setImageDrawable(drawable);
            bulletU2.setImageDrawable(drawable);
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

    public boolean onTouchEvent(MotionEvent event)
    {

        switch(event.getAction())
        {
            //case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                int xPosPlayer = (int)event.getX() - player.getWidth()/2;
                int yPosPlayer = (int)(event.getY() - player.getHeight()/2)-150;
                player.setX(xPosPlayer);
                player.setY(yPosPlayer);
                player.setRect(xPosPlayer,yPosPlayer,xPosPlayer,yPosPlayer);
                int xPosShield = (int)event.getX() - shieldPlayer.getWidth()/2;
                int yPosShield = (int)(event.getY() - shieldPlayer.getHeight()/2)-150;
                shieldPlayer.setX(xPosShield);
                shieldPlayer.setY(yPosShield);
                shieldPlayer.setRect(xPosShield,yPosShield,xPosShield,yPosShield);
                break;
        }
        return true;
    }

    /*
    //Metodo usato per il movimento dell'oggetto tramite due immagini
    public boolean onTouchEvent(MotionEvent event)
    {
        final Region region1 = new Region(left.getLeft(), left.getTop(),left.getRight(), left.getBottom());
        final Region region2 = new Region(right.getLeft(), right.getTop(),right.getRight(), right.getBottom());
        final float xRegion1 = event.getX();
        final float yRegion1 = event.getY();
        final float xRegion2 = event.getX();
        final float yRegion2 = event.getY();
        if(touchFlag == true)
        {
            if(region1.contains((int)xRegion1,(int)yRegion1))
            {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    rect_actionFlagSX = true;
                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    rect_actionFlagSX = false;
                }
            }
            else if(region2.contains((int)xRegion2,(int)yRegion2))
            {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    rect_actionFlagDX = true;
                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    rect_actionFlagDX = false;
                }
            }
            return true;
        }
        else
        {
            return false;
        }
    }*/

    @Override
    public void onSensorChanged(SensorEvent event) {
        /*
        destra x = 8 y = 1
        sinistra x = 8 y = -1
        normale x=9 y=0
        */
        if(accelerometerFlag == true)
        {
            if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && event.values[0]<8.5 && event.values[1]>1.5)
            {
                rect_actionFlagDX = true;
            }
            else
            {
                rect_actionFlagDX = false;
            }
            if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && event.values[0]<8.5 && event.values[1]<-2)
            {
                rect_actionFlagSX = true;
            }
            else
            {
                rect_actionFlagSX = false;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void writeMessage(View view)
    {
        String message = coinViewAll.getText().toString();
        String file_name = "data";
        try {
            FileOutputStream fileOutputStream = openFileOutput(file_name,MODE_PRIVATE);
            fileOutputStream.write(message.getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void readMessage(View view)
    {
        String message;
        try {
            FileInputStream fileInputStream = openFileInput("data");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();
            while((message = bufferedReader.readLine()) != null)
            {
                stringBuffer.append(message + "\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

