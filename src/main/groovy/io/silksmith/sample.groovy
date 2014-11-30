package io.silksmith;

def pathPattern = ~/\/FILES\/project\/([^\/\s]*)\/(\w+)\/(\w+)\/(\d+)\/(.*)/
def target = "/FILES/project/:todomvc/main/js/0/my/file.js"

def m = target=~pathPattern
m.matches()