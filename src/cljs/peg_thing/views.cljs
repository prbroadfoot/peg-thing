(ns peg-thing.views
  (:require [re-frame.core :as re-frame]
            [peg-thing.board :as board]))

(defn peg-color [peg-board pos]
  (cond
    (not (board/pegged? peg-board pos)) "grey"
    (= (:active-peg peg-board) pos) "blue"
    :else "red"))

(defn peg-component [pos r [x-orig y-orig]]
  (let [peg-board (re-frame/subscribe [:board])]
    [:circle {:cx (+ x-orig (* 0.866 2 r (board/x-translation pos)))
              :cy (+ y-orig (* 0.866 4 r (board/y-translation pos)))
              :r r
              :style {:fill (peg-color @peg-board pos)}
              :on-click #(re-frame/dispatch [:peg-clicked pos])}]))

(defn points
  "Returns a string formatted for the svg points attribute"
  [coords]
  (clojure.string/join " " (map (fn [[x y]] (str x "," y)) coords)))

(defn board-component [rows r]
  (let [size (* r rows 4)]
    (fn []
      (re-frame/dispatch [:board-created rows])
      [:svg {:width size :height size :style {:display "block" :margin "auto"}}
       [:polygon {:points (points [[0 size] [(/ size 2) 0] [size size]])}]
       (for [pos (range 1 (inc (board/row-tri rows)))]
         [peg-component pos r [(/ size 2) (* 4 r)]])])))

(defn reset-button []
  (let [peg-board (re-frame/subscribe [:board])]
    [:input {:type "button"
             :value "Reset"
             :on-click #(re-frame/dispatch [:reset-button-clicked])}]))

(defn status-component []
  (let [peg-board (re-frame/subscribe [:board])
        message (cond
                  (board/all-pegged? @peg-board) "Choose a peg to remove."
                  (not (board/can-move? @peg-board)) (if (= (count (board/pegged-positions @peg-board)) 1)
                                                       "You win!"
                                                       "You lose!")
                  :else "_")]
    [:h2  message]))

(defn undo-button []
    [:input {:type "button"
             :on-click #(re-frame/dispatch [:undo])
             :value "Undo"}])

(defn main-panel []
    (fn []
      [:div {:style {:position "absolute" :top "50%" :left "50%" :transform "translate(-50%, -50%)"
                     :text-align "center"}}
       [board-component 5 15]
       [:br]
       [:div [reset-button] [undo-button]]
       [status-component]
       ]))
