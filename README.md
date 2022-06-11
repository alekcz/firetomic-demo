# Getting Started with Firetomic 

[:clojureD 2022](https://clojured.de/)

In this session we're going to build an app that uses firetomic. With this app you'll be able to safe guard your loved ones from food envy. Once and for all. 
Haha, I know. It's crazy to think someone built datomic on firebase. But here we are. This will be a very practical workshop. If you're like and have always wanted to do more than store Bob's age and email address, then this is the one for you. We will not be going from 0 to a 100 real quick. We'll rather do so at a medium pace. Like when you run-walk to receive your delivery because you don't want to scare the delivery guy with your excitement.

If you're an experience datahike or datomic user this session probably isn't for you. 

## Getting started

- Clone https://github.com/alekcz/firetomic-demo.git and `cd` into it. 
- You need Java of some sort (8+ should be fine)
- You need code editor with syntax highlighting
- Install node, npm and then the firebase emulator so we don't all connect to the wifi crash it (`npm install -g firebase-tools@10.1.2`)
- Install docker
- Install leiningen https://leiningen.org/#install 
- Check you have everything `bash verify.sh` (this'll also get the docker image for you)
- Run the thing with `bash run.sh`

## Firebase
You do need a firebase account to deploy firetomic full but the session is designed in a way that you can still get a full experience without it. Coz you know, it needs internet and we don't want to bring the wifi crashing down.  

## The Workshop

The workshop will be divided into 3 sections. 

- Getting started with datahike: We'll be chatting about the basic concepts of datalog and datahike. Then we'll move on to transactions and persisting your data to firebase. 

- Querying data in datahike: With transactions under our belts we'll unlock the most exciting features of datalog databases. You know the ones, queries, pull syntax, and time-travel. 
  
- Deploying Firetomic: Lastly we'll split out our application and store by deploying firetomic. This will be final step in deploying firetomic. After this you truly tell the world: Veni, vidi, vici.

Looking forward to it. Now that you've installed what you need to and read this far your deserve a treat. Enjoy Berlin, friend. 

## Some great videos to watch before we get started (optional)
Oh, you're still here. Not keen on going outside hey. Well you could also watch these vids. They're pretty neat. 

- [Hitchhiker trees](https://youtu.be/jdn617M3-P4)
- [Domain modeling with Datalog](https://youtu.be/oo-7mN9WXTw)


## Running Firetomic locally

```bash
docker run \
  --env FIRETOMIC_NAME=clojured \
  --env FIRETOMIC_DB=http://host.docker.internal:9000 \
  --env FIRETOMIC_KEEP_HISTORY=true \
  --env FIRETOMIC_PORT=4000 \
  --env FIRETOMIC_TOKEN=foshizzle \
  --env FIRETOMIC_DEV_MODE=true \
  -p 4000:4000 \
  --add-host host.docker.internal:host-gateway \
  alekcz/firetomic:latest
```


## Stuck? Got questions?
If you get stuck or have questions, ping me in #firetomic channel on the clojurians slack. Catch y'all later. 
