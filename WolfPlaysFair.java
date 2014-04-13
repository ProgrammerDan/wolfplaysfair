import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.awt.Point;
import java.util.Set;
import java.util.HashSet;

//package animals;

public class WolfPlaysFair extends Animal {
	
	private final InternalWolf internalWolf;

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

	private static class WolfMapNode {

		public WolfMapNode(Point location) {
			up = null;
			right = null;
			down = null;
			left = null;
			latest = null;
			this.location = location;
			latestObservation = -1;
			observances = new HashMap<Character, List<Integer>>();
			likelihood = new HashMap<Character, Double>();
		}

		private Point location;

		private WolfMapNode up;
		private WolfMapNode right;
		private WolfMapNode down;
		private WolfMapNode left;

		public Point location() {
			return location;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof WolfMapNode) {
				return (((WolfMapNode) o).location().x == location.x &&
					   ((WolfMapNode) o).location().y == location.y);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return location.hashCode();
		}

		public WolfMapNode up() {
			return up;
		}
		public void up(WolfMapNode node) {
			up = node;
		}

		public WolfMapNode right() {
			return right;
		}
		public void right(WolfMapNode node) {
			right = node;
		}

		public WolfMapNode down() {
			return down;
		}
		public void down(WolfMapNode node) {
			down = node;
		}

		public WolfMapNode left() {
			return left;
		}
		public void left(WolfMapNode node) {
			left = node;
		}
		
		private Map<Character, List<Integer>> observances;
		private Map<Character, Double> likelihood;
		private Character latest;
		private int latestObservation;

		public void observe(int turn, char obj) {
			latest = obj;
			latestObservation = turn;
			likelihood.put(obj, 1.0);
			if (observances.containsKey(obj)) {
				observances.get(obj).add(turn);
			} else {
				List<Integer> obv = new ArrayList<Integer>();
				obv.add(turn);
				observances.put(obj, obv);
			}
			decay(obj);
		}
		public void noObserve(int turn) {
			latest = null;
			decay('\0');
		}
		public void decay(char except) {
			for (Character key : likelihood.keySet()) {
				if (key != except) {
					Double value = likelihood.get(key);
					if (value == null) {
						likelihood.put(key, 0.0);
					} else {
						likelihood.put(key, value*0.9 > 0.05 ? value*0.9 : 0.0);
					}
				}
			}
		}

		public int lastObservedTurn() {
			return latestObservation;
		}

		public double wolfLikelihood() {
			return likelihood.get('W');
		}
	}

	private static class WolfMap {
		WolfMapNode origin;
		WolfMapNode latest;
		Map<Point, WolfMapNode> nodeList;
		public WolfMap() {
			nodeList = new HashMap<Point, WolfMapNode>();
			origin = new WolfMapNode(new Point(0,0));
			latest = origin;
			origin.up(new WolfMapNode(new Point(0,-1)));
			origin.left(new WolfMapNode(new Point(-1,0)));
			origin.down(new WolfMapNode(new Point(0,1)));
			origin.right(new WolfMapNode(new Point(1,0)));
			nodeList.put(origin.location(), origin);
			nodeList.put(origin.up().location(), origin.up());
			nodeList.put(origin.right().location(), origin.right());
			nodeList.put(origin.down().location(), origin.down());
			nodeList.put(origin.left().location(), origin.left());
			// top left
			WolfMapNode corner = new WolfMapNode(new Point(-1,-1));
			corner.right(origin.up());
			corner.down(origin.left());
			origin.up().left(corner);
			origin.left().up(corner);
			nodeList.put(corner.location(), corner);
			// top right
			corner = new WolfMapNode(new Point(1,-1));
			corner.left(origin.up());
			corner.down(origin.right());
			origin.up().right(corner);
			origin.right().up(corner);
			nodeList.put(corner.location(), corner);
			// down left
			corner = new WolfMapNode(new Point(-1,1));
			corner.right(origin.down());
			corner.up(origin.left());
			origin.down().left(corner);
			origin.left().down(corner);
			nodeList.put(corner.location(), corner);
			// down right
			corner = new WolfMapNode(new Point(1,1));
			corner.left(origin.down());
			corner.up(origin.right());
			origin.down().right(corner);
			origin.right().down(corner);
			nodeList.put(corner.location(), corner);
		}

		public void move(Move move) {
			switch(move) {
				case LEFT:
					latest = latest.left();
					if (latest.left() != null && latest.left().up() != null &&
						latest.left().down() != null) return;
					break;
				case UP:
					latest = latest.up();
					if (latest.up() != null && latest.up().left() != null &&
						latest.up().right() != null) return;
					break;
				case RIGHT:
					latest = latest.right();
					if (latest.right() != null && latest.right().up() != null &&
						latest.right().down() != null) return;
					break;
				case DOWN:
					latest = latest.down();
					if (latest.down() != null && latest.down().left() != null &&
						latest.down().right() != null) return;
					break;
			}
			connectMap();
		}

		private void connectMap() {
			WolfMapNode wmn;

			Point location = latest.location();
			//UP
			connectNode(new Point(location.x,location.y-1));
			//LEFT
			connectNode(new Point(location.x-1,location.y));
			//DOWN
			connectNode(new Point(location.x,location.y+1));
			//RIGHT
			connectNode(new Point(location.x+1,location.y));
			//TOPLEFT
			connectNode(new Point(location.x-1,location.y-1));
			//TOPRIGHT
			connectNode(new Point(location.x+1,location.y-1));
			//BOTTOMLEFT
			connectNode(new Point(location.x-1,location.y+1));
			//BOTTOMRIGHT
			connectNode(new Point(location.x+1,location.y+1));
		}

		private void connectNode(Point pos) {
			if (!nodeList.containsKey(pos)) {
				WolfMapNode node = new WolfMapNode(pos);
				nodeList.put(pos, node);
				WolfMapNode pn = nodeList.get(new Point(node.location().x,
						node.location().y-1));
				node.up(pn);
				pn.down(node);

				pn = nodeList.get(new Point(node.location().x-1,
						node.location().y));
				node.left(pn);
				pn.right(node);

				pn = nodeList.get(new Point(node.location().x,
						node.location().y+1));
				node.down(pn);
				pn.up(node);
				
				pn = nodeList.get(new Point(node.location().x+1,
						node.location().y));
				node.right(pn);
				pn.left(node);
			}
		}

		public void update(int turn, char[][] surround) {
			Set<Point> updated = new HashSet<Point>();
			for (int i=-1;i<2;i++) {
				for (int j=-1;j<2;j++) {
					WolfMapNode wmn = nodeList.get(new Point(
							latest.location().x+i, latest.location().y+j));
					wmn.observe(turn, surround[1+j][1+i]);
					updated.add(wmn.location());
				}
			}
			// Now hit every other node with an update of no observation.
			for (Point p : nodeList.keySet()) {
				if (!updated.contains(p)) {
					nodeList.get(p).noObserve(turn);
				}
			}
		}
	}

	private static class InternalWolf {
		private static int instanceCount = 0;
		private int mapSize;
		private int turnCount;
		private List<Move> moveList;
		private WolfMap map;
		private boolean lionNext;

		protected InternalWolf(int mapSize) {
			InternalWolf.instanceCount++;
			this.mapSize = mapSize;
			this.turnCount = 0;
			this.lionNext = false;
			this.moveList = new ArrayList<Move>();
			this.map = new WolfMap();
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
				default:
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
				default:
					return Attack.SCISSORS;
			}
		}

		private void checkRandom(Random rand) {
			int samples = 0;
			int[] sum = new int[3];
			for (;samples<3000;samples++){
				sum[rand.nextInt(3)]++;
			}
			if (sum[0] < 850 || sum[1] < 850 || sum[2] < 850) {
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
			this.map.update(turnCount, map);
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

			this.map.move(move);
			return move;
		}

		//TODO
		private Move doSearch() {
			return Move.HOLD;
		}
	}
}
