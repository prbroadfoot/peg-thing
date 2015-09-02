(ns peg-thing.board)

(def tri (reductions + (rest (range))))

(defn triangular?
  "Is the number triangular? e.g. 1, 3, 6, 10, 15, etc"
  [n]
  (= n (last (take-while #(<= % n) tri))))

(defn row-tri
  "The triangular number at the end of row n"
  [n]
  (last (take n tri)))

(defn row-num
  "Returns the row number the position belongs to: pos 1 in row 1,
  positions 2 and 3 in row 2, etc"
  [pos]
  (inc (count (take-while #(> pos %) tri))))

(defn in-bounds? [max-pos & positions]
  (= max-pos (apply max max-pos positions)))

(defn connect [board max-pos pos neighbor destination]
  (if (in-bounds? max-pos pos neighbor destination)
    (reduce (fn [new-board [p1 p2]] (assoc-in new-board [p1 :connections p2] neighbor))
            board
            [[pos destination] [destination pos]])
    board))

(defn connect-right [board max-pos pos]
  (let [neighbor (inc pos)
        destination (inc neighbor)]
    (if-not (or (triangular? neighbor) (triangular? pos))
      (connect board max-pos pos neighbor destination)
      board)))

(defn connect-down-left [board max-pos pos]
  (let [row (row-num pos)
        neighbor (+ row pos)
        destination (+ 1 row neighbor)]
    (connect board max-pos pos neighbor destination)))

(defn connect-down-right [board max-pos pos]
  (let [row (row-num pos)
        neighbor (+ 1 row pos)
        destination (+ 2 row neighbor)]
    (connect board max-pos pos neighbor destination)))

(defn add-pos
  "Pegs the position and adds connections"
  [board max-pos pos]
  (let [pegged-board (assoc-in board [pos :pegged] true)]
    (reduce (fn [new-board connector] (connector new-board max-pos pos))
            pegged-board
            [connect-right connect-down-left connect-down-right])))

(defn new-board
  [rows]
  (let [initial-board {:rows rows}
        max-pos (row-tri rows)]
    (reduce (fn [board pos] (add-pos board max-pos pos))
            initial-board
            (range 1 (inc max-pos)))))

;;;;
;; Move pegs
;;;;

(defn pegged?
  [board pos]
  (get-in board [pos :pegged]))

(defn valid-moves
  "Return a map of all valid moves for pos, where the key is the
  destination and the value is the jumped position"
  [board pos]
  (into {} (filter (fn [[destination jumped]] (and (not (pegged? board destination))
                                                   (pegged? board jumped)))
                   (get-in board [pos :connections]))))

(defn valid-move?
  "Return jumped position if move from p1 to p2 is valid, nil
  otherwise."
  [board p1 p2]
  (get (valid-moves board p1) p2))

(defn remove-peg
  [board p]
  (assoc-in board [p :pegged] false))

(defn add-peg
  [board p]
  (assoc-in board [p :pegged] true))

(defn move-peg
  "Take peg out of p1 and place it in p2"
  [board p1 p2]
  (-> board
      (remove-peg p1)
      (add-peg p2)))

(defn make-move
  "Move peg from p1 to p2, removing jumped peg"
  [board p1 p2]
  (if-let [jumped (valid-move? board p1 p2)]
    (-> (move-peg (remove-peg board jumped) p1 p2)
        (assoc :active-peg nil))
    board))

(defn pegged-positions [board]
  "List all of the positions that are pegged"
  (map first (filter #(:pegged (second %)) board)))

(defn can-move?
  "Do any of the peg positions have valid moves?"
  [board]
  (some (comp not-empty (partial valid-moves board)) (pegged-positions board)))

(defn all-pegged? [board]
  (= (row-tri (:rows board))
     (count (pegged-positions board))))

;;;;
;; Calculations for drawing
;;;;

(defn row-index
  "Returns the row index of pos,
  e.g. 1 => 1, 2 => 1, 5 => 2"
  [pos]
  (let [row (row-num pos)]
    (- (+ pos row) (row-tri row))))

(defn x-translation
  [pos]
  (let [from-row (* -1 (dec (row-num pos)))
        from-row-index (* 2 (dec (row-index pos)))]
    (+ from-row from-row-index)))

(defn y-translation
  [pos]
  (* (dec (row-num pos))))
