const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
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

test("Unauthenticated user: cannot read user profiles or bets", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("bets").get());
});

test("Authenticated user: can create and read their own profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const userDoc = aliceDb.collection("users").doc(ALICE_UID);

  await assertSucceeds(
    userDoc.set({
      userId: ALICE_UID,
      email: "alice@example.com",
      displayName: "Alice",
      preferredCurrency: "USD",
      vipTier: "VIP GOLD MEMBER",
      startingBankroll: 50000,
      weeklyLossLimit: 3500,
      weeklyStakeLimit: 9000,
      createdAt: new Date(),
    })
  );

  await assertSucceeds(userDoc.get());
});

test("Authenticated user: cannot read another user's profile or bets", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(BOB_UID).set({
      userId: BOB_UID,
      preferredCurrency: "USD",
      startingBankroll: 50000,
      weeklyLossLimit: 3500,
      weeklyStakeLimit: 9000,
      createdAt: new Date(),
    });
    await context.firestore().collection("users").doc(BOB_UID).collection("bets").doc("bet_bob").set({
      id: "bet_bob",
      userId: BOB_UID,
      category: "SPORTS",
      subcategory: "Fútbol",
      eventName: "Real Madrid vs Barcelona",
      market: "Victoria Local",
      odds: 2.1,
      stake: 500,
      status: "PENDING",
      payout: 0,
      timestamp: Date.now(),
      createdAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).get());
  await assertFails(aliceDb.collection("users").doc(BOB_UID).collection("bets").doc("bet_bob").get());
  await assertFails(aliceDb.collection("users").doc(BOB_UID).collection("bets").get());
});

test("Authenticated user: can create, read, and delete their own bets", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const betDoc = aliceDb.collection("users").doc(ALICE_UID).collection("bets").doc("bet_123");

  await assertSucceeds(
    betDoc.set({
      id: "bet_123",
      userId: ALICE_UID,
      category: "SPORTS",
      subcategory: "Fútbol",
      eventName: "Champions League Final",
      market: "Over 2.5",
      odds: 1.85,
      stake: 250,
      status: "WON",
      payout: 462.5,
      timestamp: Date.now(),
      createdAt: new Date(),
    })
  );

  await assertSucceeds(betDoc.get());
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("bets").get());
  await assertSucceeds(betDoc.delete());
});

test("Schema validation: rejects invalid bet category or odds", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const betDoc = aliceDb.collection("users").doc(ALICE_UID).collection("bets").doc("bet_invalid");

  await assertFails(
    betDoc.set({
      id: "bet_invalid",
      userId: ALICE_UID,
      category: "INVALID_CAT",
      subcategory: "Fútbol",
      eventName: "Game",
      market: "1X2",
      odds: 0.5, // Invalid odds < 1.0
      stake: 100,
      status: "WON",
      payout: 50,
      timestamp: Date.now(),
      createdAt: new Date(),
    })
  );
});
