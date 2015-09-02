(ns peg-thing.handlers
    (:require [re-frame.core :as re-frame]
              [peg-thing.db :as db]
              [peg-thing.board :as board]))

(re-frame/register-handler
 :initialize-db
 (fn  [_ _]
   db/default-db))

(re-frame/register-handler
 :board-created
 (fn [app-state [_ rows]]
   (assoc app-state :board (board/new-board rows))))

(re-frame/register-handler
 :peg-clicked
 (re-frame/undoable "clicked peg")
 (fn [app-state [_ peg-pos]]
   (let [peg-board (:board app-state)
         active-peg (:active-peg peg-board)]
     (cond
       (board/all-pegged? peg-board) (assoc app-state :board (board/remove-peg peg-board peg-pos))
       (and (not active-peg)
            (board/pegged? peg-board peg-pos)) (assoc-in app-state [:board :active-peg] peg-pos)
       (= active-peg peg-pos) (assoc-in app-state [:board :active-peg] nil)
       (and active-peg
            (not= active-peg peg-pos)) (assoc app-state :board (board/make-move peg-board active-peg peg-pos))
       :else app-state))))

(re-frame/register-handler
 :reset-button-clicked
 (re-frame/undoable "reset board")
 (fn [app-state _]
   (assoc app-state :board (board/new-board (get-in app-state [:board :rows])))))
