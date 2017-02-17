(ns maze-clojure.core
  (:gen-class))

(def size 10)

(defn create-room []
  (vec
    (for [row (range size)]
      (vec
        (for [col (range size)]
          {:row row, :col col, :visited? false, :bottom? true, :right? true, :start? false :end? false})))))


(defn psbl-nybrs [rooms row col]
  (vec
    (filter
      (fn [room]
        (and room (= false (:visited? room))))
      [(get-in rooms [(dec row) col])
       (get-in rooms [(inc row) col])
       (get-in rooms [row (dec col)])
       (get-in rooms [row (inc col)])])))

(defn random-neighbor [rooms row col]
  (let [neighbors (psbl-nybrs rooms row col)]
    (if (pos? (count neighbors))
      (rand-nth neighbors)
      nil)))

(defn tear-wall [rooms oldRow oldCol newRow newCol]
  (cond
    (< newRow oldRow)
    (assoc-in rooms [newRow newCol :bottom?] false)
    (> newRow oldRow)
    (assoc-in rooms [oldRow oldCol :bottom?] false)
    (< newCol oldCol)
    (assoc-in rooms [newRow newCol :right?] false)
    (> newCol oldCol)
    (assoc-in rooms [oldRow oldCol :right?] false)))
    

(declare create-maze)

(defn create-loop [rooms oldRow oldCol newRow newCol]
  (let [new-rooms (tear-wall rooms oldRow oldCol newRow newCol)
        new-rooms (create-maze new-rooms newRow newCol)]
    (if (= rooms new-rooms)
      rooms
      (create-loop new-rooms oldRow oldCol newRow newCol))))

(defn has-no-end? [rooms]
  (= 1 (count (set (map :end? (flatten rooms))))))

(defn create-maze [rooms row col]
  (let [rooms (assoc-in rooms [row col :visited?] true)
        next-room (random-neighbor rooms row col)]
    (if next-room
      (create-loop rooms row col (:row next-room) (:col next-room))
      (if (has-no-end? rooms)
        (assoc-in rooms [row col :end?] true)
          
        rooms))))
    
  
                  

(defn -main []
  (let [rooms (create-room)
        rooms (assoc-in rooms [0 0 :start?] true)
        rooms (create-maze rooms 0 0)]
    (doseq [_ rooms]
      (print " _"))
    (println)
    (doseq [row rooms]
      (print "|")
      (doseq [room row]
        (cond
          (:start? room)
          (print "o")
          (:end? room)
          (print "x")
          :else
          (if (:bottom? room)
            (print "_")
            (print " ")))
        (if (:right? room)
          (print "|")
          (print " ")))
      (println))))
        
         
        
         
