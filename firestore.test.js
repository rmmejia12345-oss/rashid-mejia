const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read user saves", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("saves").get());
});

test("Authenticated user: can create and read their own save", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const saveDoc = aliceDb.collection("users").doc(ALICE_UID).collection("saves").doc("slot_0");
  
  await assertSucceeds(
    saveDoc.set({
      saveId: "slot_0",
      userId: ALICE_UID,
      heroName: "Sir Cedric",
      heroClass: "WARRIOR",
      level: 1,
      zone: "OAKHAVEN_VILLAGE",
      currentHp: 220,
      maxHp: 220,
      createdAt: new Date(),
      updatedAt: new Date()
    })
  );

  await assertSucceeds(saveDoc.get());
});

test("Authenticated user: cannot read another user's save", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(BOB_UID).collection("saves").doc("slot_0").set({
      saveId: "slot_0",
      userId: BOB_UID,
      heroName: "Bob Knight",
      heroClass: "WARRIOR",
      level: 5,
      zone: "WHISPERING_WOODS",
      createdAt: new Date(),
      updatedAt: new Date()
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).collection("saves").doc("slot_0").get());
});

test("Leaderboard: any authenticated user can read, but only owner can write", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();

  // Alice sets her leaderboard entry
  await assertSucceeds(
    aliceDb.collection("leaderboard").doc(ALICE_UID).set({
      userId: ALICE_UID,
      heroName: "Sir Cedric",
      heroClass: "WARRIOR",
      level: 10,
      bossTrophies: 3,
      dungeonClears: 12,
      createdAt: new Date(),
      updatedAt: new Date()
    })
  );

  // Bob can read Alice's leaderboard entry
  await assertSucceeds(bobDb.collection("leaderboard").doc(ALICE_UID).get());

  // Bob cannot overwrite Alice's leaderboard entry
  await assertFails(
    bobDb.collection("leaderboard").doc(ALICE_UID).set({
      userId: BOB_UID,
      heroName: "Hacker Bob",
      heroClass: "WARRIOR",
      level: 99,
      bossTrophies: 99,
      dungeonClears: 99,
      createdAt: new Date(),
      updatedAt: new Date()
    })
  );
});
