(ns peg-thing.core-test
  (:require
   [cljs.test :refer-macros [deftest testing is]]
   [peg-thing.core :as core]))

(deftest fake-test
  (testing "fake description"
    (is (= 1 2))))
