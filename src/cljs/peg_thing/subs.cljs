(ns peg-thing.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]
              [peg-thing.board :as board]))

(re-frame/register-sub
 :board
 (fn [db]
   (reaction (:board @db))))
