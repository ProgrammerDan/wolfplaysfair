package animals;

public class WolfPlaysFair extends Animal {
	
	private final internalWolf;

	public WolfPlaysFair() {
		super('W');

		internalWolf = new InternalWolf(MAP_SIZE);
	}
	
	@Override
	public Attack fight(char opponent) {
		return internalWolf.attack(opponent);
	}

	@Override
	public Move move() {
		return internalWolf.move(surroundings);
	}

	private static InternalWolf {
		private static int instanceCount = 0;
		private int mapSize;
		private int turnCount;
		protected InternalWolf(int mapSize) {
			InternalWolf.instanceCount++;
			this.mapSize = mapSize;
			this.turnCount = 0;
			if (InternalWolf.instanceCount >= 100) {
				throw new RuntimeException("You're trying to instantiate a Play Fair Wolf for nefarious purposes, aren't you? Naughty, naughty.");
			}
		}

		protected Attack attack(char opponent) {
			checkAttacks();
			switch (opponent) {
				case 'B':
				case 'L':
					return Attack.SCISSORS;
				case 'S':
					return Attack.PAPER;
				case 'W':
					return randomAttack();
				else:
					throw new RuntimeException("No fair. Who is picking an invalid character for themselves? I quit.");
			}
		}

		private Attack randomAttack() {
			Random rand = new Random(System.nanoTime());
			checkRandom(rand);

			switch(rand.nextInt(3)) {
				case 0:
					return Attack.ROCK;
				case 1:
					return Attack.PAPER;
				else:
					return Attack.SCISSORS;
			}
		}

		private void checkRandom(Random rand) {
			int samples = 0;
			int sum = 0;
			for (;samples<1000;samples++){
				sum += rand.nextInt(3);
			}
			if (sum < 250 || sum > 750) {
				throw new RuntimeException("This random generator hijacking is getting old, kids. Bye!");
			}
		}

		private void checkAttacks() {
			if (Attack.ROCK.ordinal() == 0 && Attack.PAPER.ordinal() == 1 &&
				Attack.SCISSORS.ordinal() == 2 && Attack.SUICIDE.ordinal() == 3) {
				return;
			}
			throw new RuntimeException("Someone messed with the attack ordinals! This is B.S., I refuse to participate with such a blowhard.");
		}

		private void checkMoves() {
			if (Move.UP.ordinal() == 0 && Move.RIGHT.ordinal() == 1 &&
				Move.DOWN.ordinal() == 2 && Move.LEFT.ordinal() == 3 &&
				Move.HOLD.ordinal() == 4) {
				return;
			}
			throw new RuntimeException("Now they're messing with the Move ordinals?! Sigh. I'm taking my ball and going home.");
		}

		protected Move move(char[][] map) {
			checkMoves();
			turnCount++;
			Move move = null;
			if (lionNext) {
				lionNext = false;
				if (map[0][1] == 'L') {
					move = Move.UP;
				} else if (map[1][0] == 'L') {
					move = Move.LEFT;
				}
			}

			if (map[0][0] == 'L') {
				lionNext = true;
			}

			if (turnCount < 250) {
				if (move == null) {
					move = Move.HOLD;
				}
			} else if (move != null && turnCount >= 250) {
				move = doSearch();
			}

			return move;
		}

		//TODO
		private Move doSearch() {
			return Move.HOLD;
		}
	}
}
